package com.digigate.engineeringmanagement.storemanagement.fileupload;

import com.digigate.engineeringmanagement.common.config.model.ExcelData;
import com.digigate.engineeringmanagement.common.config.model.ExcelDataResponse;
import com.digigate.engineeringmanagement.common.config.util.ExcelFileUtil;
import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.initializerservice.StoreLifeTypeLoader;
import com.digigate.engineeringmanagement.planning.constant.PartClassification;
import com.digigate.engineeringmanagement.planning.constant.PartStatus;
import com.digigate.engineeringmanagement.planning.constant.StorePartAvailabilityLogParentType;
import com.digigate.engineeringmanagement.planning.entity.Part;
import com.digigate.engineeringmanagement.planning.payload.response.PartViewModelLite;
import com.digigate.engineeringmanagement.planning.service.PartService;
import com.digigate.engineeringmanagement.storemanagement.constant.TransactionType;
import com.digigate.engineeringmanagement.storemanagement.converter.FileUploadConverter;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.StorePartAvailabilityLog;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.StorePartSerial;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.StorePartSerialInternalDto;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.StorePartAvailabilityLogService;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.StorePartSerialService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.*;
import static com.digigate.engineeringmanagement.common.util.Helper.toInt;
import static com.digigate.engineeringmanagement.planning.constant.StorePartAvailabilityLogParentType.DEMAND;
import static com.digigate.engineeringmanagement.storemanagement.constant.TransactionType.RECEIVE;
import static java.util.Optional.ofNullable;

/**
 * Inventory Report Service
 *
 * @author Sayem Hasnat
 */
@Service
public class InventoryReportService {
    private final Environment environment;
    private final StorePartAvailabilityLogService partAvailabilityLogService;
    private final StorePartSerialService storePartSerialService;
    private final PartService partService;
    private final DateTimeFormatter SLASH_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public InventoryReportService(Environment environment, StorePartAvailabilityLogService partAvailabilityLogService,
                                  StorePartSerialService storePartSerialService, PartService partService) {
        this.environment = environment;
        this.partAvailabilityLogService = partAvailabilityLogService;
        this.storePartSerialService = storePartSerialService;
        this.partService = partService;
    }

    public Object uploadExcel(MultipartFile file, List<String> sheetNameList,Long aircraftModelId ) {

        Map<String, List<InventoryReportDto>> sheetListMap = new LinkedHashMap<>();
        Map<String, ExcelDataResponse> excelDataResponseListMap = new LinkedHashMap<>();
        for (String sheet : sheetNameList) {
            ExcelData excelData = ExcelFileUtil
                    .getExcelDataFromSheet(file, environment.getProperty(InventoryReportConstant.ARM_EXCEL_PART), sheet);
            if (CollectionUtils.isNotEmpty(excelData.getErrorMessages())) {
                excelDataResponseListMap.put(sheet, ExcelFileUtil.prepareErrorResponse(excelData.getErrorMessages()));
            } else {
                sheetListMap.put(sheet, prepareInventoryReportDto(excelData.getDataList()));
                excelDataResponseListMap.put(sheet, ExcelFileUtil.prepareSuccessResponse());
            }
        }
        saveInventoryReport(sheetListMap,aircraftModelId);
        return excelDataResponseListMap;
    }

    /**
     * This method will convert Inventory report excel dto to Inventory Report Dto
     *
     * @param dataList {@link List}`
     */
    private List<InventoryReportDto> prepareInventoryReportDto(List<Map<String, ?>> dataList) {
        List<InventoryReportDto> inventoryReportDtoList = new ArrayList<>();
        for (Map<String, ?> dataMap : dataList) {
            InventoryReportDto inventoryReportDto = new InventoryReportDto();
            int rowNumber = Integer.parseInt(String.valueOf(dataMap.get(ApplicationConstant.ROW_NUMBER)));
            inventoryReportDto.setRowNumber(rowNumber);
            inventoryReportDto.setSn((long) (rowNumber - 1));
            inventoryReportDto.setPartNumber((String) dataMap.get(InventoryReportConstant.PART_NUMBER));
            inventoryReportDto.setName((String) dataMap.get(InventoryReportConstant.NAME));
            inventoryReportDto.setLotNumber((String) dataMap.get(InventoryReportConstant.SERIAL_NUMBER));
            inventoryReportDto.setShLife((String) dataMap.get(InventoryReportConstant.SH_LIFE));
            inventoryReportDto.setDate((String) dataMap.get(InventoryReportConstant.DATE));
            if (dataMap.get(InventoryReportConstant.SERIAL_NUMBER) != null) {
                inventoryReportDto.setSerialNumber(prepareSerialList((String) dataMap.get(InventoryReportConstant.SERIAL_NUMBER)));
            } else {
                inventoryReportDto.setSerialNumber(Collections.singletonList(ApplicationConstant.EMPTY_STRING));
            }
            if (dataMap.containsKey(InventoryReportConstant.UNIT)) {
                inventoryReportDto.setUnit((String) dataMap.get(InventoryReportConstant.UNIT));
            }
            if (dataMap.containsKey(InventoryReportConstant.UNIT_PRICE)) {
                inventoryReportDto.setUPriceUSD((String) dataMap.get(InventoryReportConstant.UNIT_PRICE));
            }
            if (dataMap.containsKey(InventoryReportConstant.OPENING_QTY)) {
                inventoryReportDto.setOpeningQty((double) dataMap.get(InventoryReportConstant.OPENING_QTY));
            }
            if (dataMap.containsKey(InventoryReportConstant.OPENING_VALUE)) {
                inventoryReportDto.setOpeningValueUsd((String) dataMap.get(InventoryReportConstant.OPENING_VALUE));
            }
            if (dataMap.containsKey(InventoryReportConstant.PURCHASED_QTY)) {
                inventoryReportDto.setPurchasedQty((double) dataMap.get(InventoryReportConstant.PURCHASED_QTY));
            }
            if (dataMap.containsKey(InventoryReportConstant.PURCHASED_VALUE)) {
                inventoryReportDto.setPurchasedValueUsd((String) dataMap.get(InventoryReportConstant.PURCHASED_VALUE));
            }
            if (dataMap.containsKey(InventoryReportConstant.ISSUED_QTY)) {
                inventoryReportDto.setIssuedQty((double) dataMap.get(InventoryReportConstant.ISSUED_QTY));
            }
            if (dataMap.containsKey(InventoryReportConstant.ISSUED_VALUE)) {
                inventoryReportDto.setIssuedValueUsd((String) dataMap.get(InventoryReportConstant.ISSUED_VALUE));
            }
            if (dataMap.containsKey(InventoryReportConstant.QTY)) {
                inventoryReportDto.setQty((double) dataMap.get(InventoryReportConstant.QTY));
            }
            if (dataMap.containsKey(InventoryReportConstant.CLOSING_QTY)) {
                inventoryReportDto.setClosingQty((double) dataMap.get(InventoryReportConstant.CLOSING_QTY));
            }
            if (dataMap.containsKey(InventoryReportConstant.CLOSING_VALUE)) {
                inventoryReportDto.setClosingValueUsd((String) dataMap.get(InventoryReportConstant.CLOSING_VALUE));
            }
            if (dataMap.containsKey(InventoryReportConstant.ISSUED_AIRCRAFT)) {
                inventoryReportDto.setIssuedAc((String) dataMap.get(InventoryReportConstant.ISSUED_AIRCRAFT));
            }
            if (dataMap.containsKey(InventoryReportConstant.LOCATION)) {
                inventoryReportDto.setLocation((String) dataMap.get(InventoryReportConstant.LOCATION));
            }
            if (dataMap.containsKey(InventoryReportConstant.ALT_P_NO)) {
                inventoryReportDto.setAltPNo((String) dataMap.get(InventoryReportConstant.ALT_P_NO));
            }
            if (dataMap.containsKey(InventoryReportConstant.GRN)) {
                List<String> grnList = (List<String>) dataMap.get(InventoryReportConstant.GRN);
                inventoryReportDto.setGrn(grnList);
                if (CollectionUtils.isNotEmpty(grnList)) {
                    String grnAsString = grnList.toString();
                    inventoryReportDto.setGrnConsumable(grnAsString.substring
                            (ApplicationConstant.INT_ONE, grnAsString.length() - 1));
                }
            }
            inventoryReportDtoList.add(inventoryReportDto);
        }
        return inventoryReportDtoList;
    }

    /**
     * This method will return Serial List of inventoryReport
     *
     * @param string {@link String}
     */
    private List<String> prepareSerialList(String string) {
        String serialNo = string.replaceAll(ApplicationConstant.SERIAL_HEAD, ApplicationConstant.EMPTY_STRING).trim();
        String[] arrOfSerial = serialNo.split(ApplicationConstant.COMMA_SEPARATOR);
        return new ArrayList<>(Arrays.asList(arrOfSerial));
    }

    private void saveInventoryReport(Map<String, List<InventoryReportDto>> sheetListMap,Long aircraftModelId ) {
        List<StorePartAvailabilityLog> storePartAvailabilityLogs = sheetListMap.entrySet().stream().flatMap(entry ->
                entry.getValue().stream().map(val -> executeInventoryUpdate(val, aircraftModelId)).flatMap(Collection::stream))
                .collect(Collectors.toList());
        partAvailabilityLogService.saveItemList(storePartAvailabilityLogs);
    }

    //TODO
    private List<StorePartAvailabilityLog> executeInventoryUpdate(InventoryReportDto item, Long aircraftModelId) {
        if (Objects.isNull(item.getIssuedQty()) && Objects.isNull(item.getPurchasedQty())) {
            return Collections.emptyList();
        }

        List<StorePartAvailabilityLog> logList = new ArrayList<>();
        PartViewModelLite partViewModelLite;

        if (aircraftModelId != VALUE_ZERO) {
            partViewModelLite = partService.findByPartNoAndAircraftModelId(item.getPartNumber(), aircraftModelId);
        }else {
            partViewModelLite = partService.getByPartNo(item.getPartNumber());
        }
        if (Objects.nonNull(partViewModelLite)) {
            Part part = partService.findByPartNo(partViewModelLite.getPartNo());
            if (Objects.nonNull(part)) {
                item.setPart(part);
                StorePartAvailabilityLog availabilityLog = populateCommonPartData(item);
                if (part.getClassification() == PartClassification.CONSUMABLE) {
                    logList.add(populateConsumablePartData(availabilityLog, item));
                } else {
                    int grnIndex = 0;
                    for (String serial : item.getSerialNumber()) {
                        if (item.getGrn() != null) {
                            logList.add(populateComponentPartData(availabilityLog, item, serial, item.getGrn().get(grnIndex)));
                            grnIndex++;
                        }
                    }
                }
            }
        }

        return logList;
    }

    private StorePartAvailabilityLog populateComponentPartData(StorePartAvailabilityLog storePartAvailabilityLog,
                                                               InventoryReportDto item, String serial, String grn) {
        boolean isIssue = isInputtingIssue(item.getIssuedQty());
        storePartAvailabilityLog.setQuantity(ApplicationConstant.INT_ONE);
        storePartAvailabilityLog.setTransactionType(isIssue ? TransactionType.ISSUE : RECEIVE);
        storePartAvailabilityLog.setGrnNo(grn);
        storePartAvailabilityLog.setUnitPrice(Double.valueOf(item.getUPriceUSD()));

        StorePartSerialInternalDto storePartSerialInternalDto = populateToSerialRequestDto(item, storePartAvailabilityLog, serial);
        StorePartSerial storePartSerial = storePartSerialService.findAndUpdateStorePartSerial(storePartSerialInternalDto);
        storePartAvailabilityLog.setStorePartSerial(storePartSerial);

        return storePartAvailabilityLog;
    }

    private StorePartAvailabilityLog populateConsumablePartData(StorePartAvailabilityLog storePartAvailabilityLog, InventoryReportDto item) {

        storePartAvailabilityLog.setQuantity(isInputtingIssue(item.getIssuedQty()) ? toInt(item.getIssuedQty()) : toInt(item.getPurchasedQty()));
        storePartAvailabilityLog.setTransactionType(isInputtingIssue(item.getIssuedQty()) ? TransactionType.ISSUE : RECEIVE);
        storePartAvailabilityLog.setGrnNo(item.getGrnConsumable());
        storePartAvailabilityLog.setUnitPrice(Double.valueOf(item.getUPriceUSD()));

        StorePartSerialInternalDto storePartSerialInternalDto = populateToSerialRequestDto(item, storePartAvailabilityLog, item.getLotNumber());
        StorePartSerial storePartSerial = storePartSerialService.findAndUpdateStorePartSerial(storePartSerialInternalDto);
        storePartAvailabilityLog.setStorePartSerial(storePartSerial);

        return storePartAvailabilityLog;
    }

    private StorePartAvailabilityLog populateCommonPartData(InventoryReportDto item) {
        StorePartAvailabilityLog storePartAvailabilityLog = new StorePartAvailabilityLog();

        populateDateInfos(item, storePartAvailabilityLog);
        storePartAvailabilityLog.setParentType(isInputtingIssue(item.getIssuedQty()) ? StorePartAvailabilityLogParentType.ISSUE : DEMAND);
        storePartAvailabilityLog.setPartStatus(PartStatus.SERVICEABLE);
        storePartAvailabilityLog.setUnitPrice(getUnitPrice(item));
        storePartAvailabilityLog.setIssuedAc(item.getIssuedAc());
        storePartAvailabilityLog.setLocation(item.getLocation());
        return storePartAvailabilityLog;
    }

    private void populateDateInfos(InventoryReportDto item, StorePartAvailabilityLog storePartAvailabilityLog) {
        try {
            final String shLife = item.getShLife();
            if (FileUploadConverter.isDate(shLife)) {
                storePartAvailabilityLog.setShelfLife(LocalDate.parse(FileUploadConverter.convertNastyStringToData(shLife),
                        SLASH_DATE_FORMATTER));
                storePartAvailabilityLog.setSelfLifeType(SH_LIFE_TYPE_DATE);
            } else {
                storePartAvailabilityLog.setSelfLifeType(buildSelfLifeType(shLife));
            }
        } catch (Exception exception) {
            throw EngineeringManagementServerException
                    .badRequest(ApplicationConstant.COULD_NOT_PARSE_DATA + exception.getMessage());
        }
        if (StringUtils.isNotBlank(item.getDate())) {
            storePartAvailabilityLog.setReceiveDate(LocalDate.parse((FileUploadConverter.convertNastyStringToData(item.getDate())),
                    SLASH_DATE_FORMATTER));
        }
        else storePartAvailabilityLog.setReceiveDate(null);
    }

    private Double getUnitPrice(InventoryReportDto item) {
        final String priceUSD = ofNullable(item.getUPriceUSD()).orElse(DOUBLE_ZERO_STRING);
        return Double.valueOf(priceUSD.replaceAll(COMMA_SEPARATOR, EMPTY_STRING));
    }

    private String buildSelfLifeType(String shLife) {
        final Map<String, String> lifeTypeMap = StoreLifeTypeLoader.getLifeTypes();
        if (StringUtils.isNotBlank(shLife)) {
            shLife = shLife.trim().replaceAll(SHELF_LIFE_VALIDATION_REGEX, EMPTY_STRING);
            String key = lifeTypeMap.keySet().stream().filter(shLife.toUpperCase()::contains).findFirst().
                    orElse(NOT_APPLICABLE_KEY);
            return lifeTypeMap.get(key);
        }
        return lifeTypeMap.get(NOT_APPLICABLE_KEY);
    }

    private boolean isInputtingIssue(Double issuedQty) {
        return issuedQty != null && issuedQty > 0;
    }

    private StorePartSerialInternalDto populateToSerialRequestDto(InventoryReportDto item,
                                                                  StorePartAvailabilityLog storePartAvailabilityLog,
                                                                  String serial) {

        return StorePartSerialInternalDto.builder()
                .unitPrice(storePartAvailabilityLog.getUnitPrice())
                .grnNo(storePartAvailabilityLog.getGrnNo())
                .partStatus(storePartAvailabilityLog.getPartStatus())
                .parentType(storePartAvailabilityLog.getParentType())
                .shelfLife(storePartAvailabilityLog.getShelfLife())
                .partNo(item.getPartNumber())
                .part(item.getPart())
                .serialNo(serial)
                .quantity(storePartAvailabilityLog.getQuantity())
                .transactionType(storePartAvailabilityLog.getTransactionType())
                .build();
    }
}
