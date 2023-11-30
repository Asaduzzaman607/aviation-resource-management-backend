package com.digigate.engineeringmanagement.planning.service;

import com.digigate.engineeringmanagement.common.config.model.ExcelData;
import com.digigate.engineeringmanagement.common.config.model.ExcelDataResponse;
import com.digigate.engineeringmanagement.common.config.util.ExcelFileUtil;
import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.service.AbstractService;
import com.digigate.engineeringmanagement.common.util.Helper;
import com.digigate.engineeringmanagement.common.util.StringUtil;
import com.digigate.engineeringmanagement.configurationmanagement.service.aircraftinformation.AircraftModelService;
import com.digigate.engineeringmanagement.planning.constant.PartClassification;
import com.digigate.engineeringmanagement.planning.dto.request.SerialPartSearchDto;
import com.digigate.engineeringmanagement.planning.dto.request.SerialRequestDto;
import com.digigate.engineeringmanagement.planning.entity.Model;
import com.digigate.engineeringmanagement.planning.entity.Part;
import com.digigate.engineeringmanagement.planning.entity.Serial;
import com.digigate.engineeringmanagement.planning.payload.response.SerialListViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.SerialResponseDto;
import com.digigate.engineeringmanagement.planning.payload.response.SerialResponseView;
import com.digigate.engineeringmanagement.planning.payload.response.SerialViewModel;
import com.digigate.engineeringmanagement.planning.repository.SerialRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.MDC;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SerialService extends AbstractService<Serial, SerialRequestDto> {
    private final PartService partService;
    private final SerialRepository repository;

    private final AircraftModelService aircraftModelService;

    private final Environment environment;

    private static final Integer SERIAL_MAX_LENGTH = 50;
    private static final String SERIAL = "Serial";
    private static final String ARM_EXCEL_SERIAL = "arm.excel.upload.file.name.serial";
    private static final String PART_NO = "Part Number";
    private static final String SERIAL_NO = "Serial Number";
    private static final String MODEL_NAME = "Model Name";

    public SerialService(SerialRepository repository, PartService partService, AircraftModelService aircraftModelService, Environment environment) {
        super(repository);
        this.partService = partService;
        this.repository = repository;
        this.aircraftModelService = aircraftModelService;
        this.environment = environment;
    }

    public Serial validateAndPopulateEntity(SerialRequestDto serialRequestDto, Serial serial) {
        Part part = partService.findById(serialRequestDto.getPartId());
        validatePartAndSerial(serialRequestDto, serial);
        serial.setSerialNumber(serialRequestDto.getSerialNumber());
        serial.setPart(part);
        return serial;
    }

    @Override
    protected SerialResponseDto convertToResponseDto(Serial serial) {
        SerialResponseDto responseDto = new SerialResponseDto();
        responseDto.setId(serial.getId());
        responseDto.setSerialNumber(serial.getSerialNumber());
        responseDto.setModelId(Objects.nonNull(serial.getPart().getModel()) ? serial.getPart().getModel().getId() : null);
        responseDto.setModelName(Objects.nonNull(serial.getPart().getModel()) ? serial.getPart().getModel().getModelName() : null);
        responseDto.setPartId(Objects.nonNull(serial.getPart()) ? serial.getPart().getId() : null);
        responseDto.setPartNo(Objects.nonNull(serial.getPart()) ? serial.getPart().getPartNo() : null);
        responseDto.setAircraftModelId(Objects.nonNull(serial.getPart().getModel()) ? serial.getPart().getModel().getAircraftModelId() : null);
        responseDto.setClassification(serial.getPart().getClassification());
        responseDto.setIsActive(serial.getIsActive());
        return responseDto;
    }

    @Override
    protected Serial convertToEntity(SerialRequestDto serialRequestDto) {
        return validateAndPopulateEntity(serialRequestDto, new Serial());
    }

    @Override
    protected Serial updateEntity(SerialRequestDto dto, Serial entity) {
        return validateAndPopulateEntity(dto, entity);
    }

    private void validatePartAndSerial(SerialRequestDto serialRequestDto, Serial exSerial) {
        partService.findById(serialRequestDto.getPartId());
        if (serialRequestDto.getSerialNumber().length() > SERIAL_MAX_LENGTH) {
            throw new EngineeringManagementServerException(ErrorId.SERIAL_MAX_LENGTH_EXCEED, HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID));
        }
        Optional<Serial> serial = repository.findByPartIdAndSerialNumber(serialRequestDto.getPartId(),
                serialRequestDto.getSerialNumber());
        if (serial.isPresent() && Objects.nonNull(exSerial) && !Objects.equals(serial.get().getId(), exSerial.getId())) {
            throw new EngineeringManagementServerException(ErrorId.SERIAL_NO_ALREADY_EXIST, HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID));
        }
    }

    public Optional<Serial> findSerialByPartIdAndSerialNo(Long id, String serialNo) {
        return repository.findByPartIdAndSerialNumber(id, serialNo);
    }


    public List<SerialResponseView> getSerialListByPartId(Long partId) {
        return repository.findAllByPartId(partId);
    }

    public ExcelDataResponse uploadExcel(MultipartFile file, Long aircraftModelId) {
        ExcelData excelData = ExcelFileUtil
                .getExcelDataFromSheet(file, environment.getProperty(ARM_EXCEL_SERIAL), SERIAL);

        if (CollectionUtils.isNotEmpty(excelData.getErrorMessages())) {
            return ExcelFileUtil.prepareErrorResponse(excelData.getErrorMessages());
        }
        List<String> errorMessage = validateAndPrepareEntity(excelData, aircraftModelId);

        if (CollectionUtils.isNotEmpty(errorMessage)) {
            return ExcelFileUtil.prepareErrorResponse(errorMessage);
        }
        return ExcelFileUtil.prepareSuccessResponse();
    }

    private List<String> validateAndPrepareEntity(ExcelData excelData, Long aircraftModelId) {
        List<String> errorMessages = new ArrayList<>();
        if (CollectionUtils.isEmpty(excelData.getDataList())) {
            return Collections.emptyList();
        }

        List<Part> partList = partService.findAllByAircraftModelId(aircraftModelId);
        Map<String, Part> partMap = partList.stream()
                .collect(Collectors.toMap(part -> part.getPartNo() +
                        ApplicationConstant.SEPARATOR + prepareModelKey(part.getModel()), Function.identity()));

        List<Serial> existingSerial = repository.findAll();
        Map<String, Serial> serialMap = existingSerial.stream()
                .collect(Collectors.toMap(serial -> serial.getSerialNumber()
                                + ApplicationConstant.SEPARATOR + serial.getPart().getPartNo() +
                                ApplicationConstant.SEPARATOR + prepareModelKey(serial.getPart().getModel()),
                        Function.identity()));

        List<Map<String, ?>> dataList = excelData.getDataList();
        List<Serial> serialList = new ArrayList<>();
        for (Map<String, ?> dataMap : dataList) {
            String serialNo = StringUtil.parseStringNumber(StringUtil.valueOf(dataMap.get(SERIAL_NO)));
            String partNo = StringUtil.parseStringNumber(StringUtil.valueOf(dataMap.get(PART_NO)));
            String modelName = StringUtil.valueOf(dataMap.get(MODEL_NAME));
            Part part = partMap.get(partNo + ApplicationConstant.SEPARATOR + modelName);

            if (Objects.isNull(part)) {
                errorMessages.add(String.format("Part: {%s} no is not exists, Row: {%s}",
                        partNo, StringUtil.valueOf(dataMap.get(ApplicationConstant.ROW_NUMBER))));
            }

            if (serialMap.containsKey(serialNo +
                    ApplicationConstant.SEPARATOR + partNo + ApplicationConstant.SEPARATOR + modelName)) {
                errorMessages.add(String.format("Duplicate entry found for same part no and serial no. " +
                                "Serial:{%s} and part {%s} at row: {%s}", serialNo, partNo,
                        StringUtil.valueOf(dataMap.get(ApplicationConstant.ROW_NUMBER))));
            }

            Serial serial = new Serial();
            serial.setPart(part);
            serial.setSerialNumber(serialNo);
            serialList.add(serial);
        }

        if (CollectionUtils.isEmpty(errorMessages) && CollectionUtils.isNotEmpty(serialList)) {
            saveItemList(serialList);
        }

        return errorMessages;
    }

    private String prepareModelKey(Model model) {
        if (Objects.isNull(model)) {
            return null;
        }
        return model.getModelName();
    }

    public void delete(Serial serial) {
        repository.delete(serial);
    }

    public PageData searchBySerialNo(SerialPartSearchDto serialPartSearchDto, Pageable pageable) {
        Page<SerialViewModel> serialViewModels = repository.findSerial(serialPartSearchDto.getSerialNumber(),
                serialPartSearchDto.getPartId(), serialPartSearchDto.getPartNo(), serialPartSearchDto.getIsActive(), pageable);

        return PageData.builder()
                .model(serialViewModels.getContent())
                .totalPages(serialViewModels.getTotalPages())
                .totalElements(serialViewModels.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();

    }

    public PageData searchSerialNoByPartId(SerialPartSearchDto serialPartSearchDto, Pageable pageable) {
        Page<SerialViewModel> serialViewModels = repository.findSerial(serialPartSearchDto.getSerialNumber(),
                serialPartSearchDto.getPartId(), serialPartSearchDto.getPartNo(), serialPartSearchDto.getIsActive(), pageable);

        return PageData.builder()
                .model(serialViewModels.getContent().stream().map(this::populateToSerialResponse).collect(Collectors.toList()))
                .totalPages(serialViewModels.getTotalPages())
                .totalElements(serialViewModels.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();

    }

    private SerialResponseView populateToSerialResponse(SerialViewModel serialViewModel) {
        return SerialResponseView.builder().serialId(serialViewModel.getId()).serialNo(serialViewModel.getSerialNumber()).build();
    }
    public List<Serial> findAllByIdIn(Set<Long> ids) {
        return repository.findAllByIdIn(ids);
    }

    public Optional<Serial> findByIdAndPartId(Long serialId, Long partId) {
        return repository.findByIdAndPartIdAndIsActiveTrue(serialId, partId);
    }

    public List<SerialListViewModel> findAllSerialByList() {
        return repository.findAllSerialByList();
    }
}
