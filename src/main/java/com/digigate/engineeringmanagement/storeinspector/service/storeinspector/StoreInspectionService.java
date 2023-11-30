package com.digigate.engineeringmanagement.storeinspector.service.storeinspector;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.constant.VoucherType;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.configurationmanagement.dto.projection.VendorProjection;
import com.digigate.engineeringmanagement.planning.constant.StorePartAvailabilityLogParentType;
import com.digigate.engineeringmanagement.planning.entity.Part;
import com.digigate.engineeringmanagement.planning.entity.Serial;
import com.digigate.engineeringmanagement.planning.service.PartService;
import com.digigate.engineeringmanagement.planning.service.SerialService;
import com.digigate.engineeringmanagement.procurementmanagement.constant.RfqType;
import com.digigate.engineeringmanagement.procurementmanagement.constant.VendorRequestType;
import com.digigate.engineeringmanagement.procurementmanagement.dto.response.VendorQuotationInvoiceDetailViewModel;
import com.digigate.engineeringmanagement.procurementmanagement.service.VendorQuotationInvoiceDetailService;
import com.digigate.engineeringmanagement.status.entity.DemandStatus;
import com.digigate.engineeringmanagement.status.service.DemandStatusService;
import com.digigate.engineeringmanagement.storeinspector.constant.InspectionApprovalStatus;
import com.digigate.engineeringmanagement.storeinspector.entity.storeinspector.InspectionCriterion;
import com.digigate.engineeringmanagement.storeinspector.entity.storeinspector.StoreInspection;
import com.digigate.engineeringmanagement.storeinspector.entity.storeinspector.StoreInspectionGrn;
import com.digigate.engineeringmanagement.storeinspector.payload.projection.PlanningSiProjection;
import com.digigate.engineeringmanagement.storeinspector.payload.projection.StoreInspectionProjection;
import com.digigate.engineeringmanagement.storeinspector.payload.request.storeinspector.StoreInspectionRequestDto;
import com.digigate.engineeringmanagement.storeinspector.payload.response.storeinspector.InspectionCriterionResponseDto;
import com.digigate.engineeringmanagement.storeinspector.payload.response.storeinspector.StoreInspectionResponseDto;
import com.digigate.engineeringmanagement.storeinspector.repository.storeinspector.StoreInspectionRepository;
import com.digigate.engineeringmanagement.storemanagement.constant.TransactionType;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.ReturnPartsDetail;
import com.digigate.engineeringmanagement.storemanagement.entity.partsreceive.StoreStockInward;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.UnitMeasurement;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.StorePartSerial;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.StoreReturnPart;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.PartProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.StoreStockInwardProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.request.PartAvailUpdateInternalDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.StorePartSerialRequestDto;
import com.digigate.engineeringmanagement.storemanagement.payload.response.StoreSerialIdNoDto;
import com.digigate.engineeringmanagement.storemanagement.service.StoreVoucherTrackingService;
import com.digigate.engineeringmanagement.storemanagement.service.partsreceive.StoreStockInwardService;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.ReturnPartsDetailService;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.StorePartAvailabilityService;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.StorePartSerialService;
import com.digigate.engineeringmanagement.storemanagement.util.SortChanger;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.*;

@Service
public class StoreInspectionService extends AbstractSearchService<StoreInspection, StoreInspectionRequestDto, IdQuerySearchDto> {
    private final StoreInspectionRepository storeInspectionRepository;
    private final PartService partService;
    private final InspectionCriterionService inspectionCriterionService;
    private final StoreVoucherTrackingService storeVoucherTrackingService;
    private final StorePartSerialService storePartSerialService;
    private final StoreStockInwardService storeStockInwardService;
    private final ReturnPartsDetailService returnPartsDetailService;
    private final StorePartAvailabilityService storePartAvailabilityService;
    private final VendorQuotationInvoiceDetailService invoiceDetailService;
    private final SerialService serialService;
    private final DemandStatusService demandStatusService;
    private final StoreInspectionGrnService storeInspectionGrnService;

    @Autowired
    public StoreInspectionService(AbstractRepository<StoreInspection> repository,
                                  StoreInspectionRepository storeInspectionRepository,
                                  PartService partService,
                                  @Lazy InspectionCriterionService inspectionCriterionService,
                                  StoreVoucherTrackingService storeVoucherTrackingService,
                                  StorePartSerialService storePartSerialService,
                                  StoreStockInwardService storeStockInwardService,
                                  ReturnPartsDetailService returnPartsDetailService,
                                  StorePartAvailabilityService storePartAvailabilityService,
                                  VendorQuotationInvoiceDetailService invoiceDetailService,
                                  SerialService serialService,
                                  DemandStatusService demandStatusService,
                                  StoreInspectionGrnService storeInspectionGrnService) {
        super(repository);
        this.storeInspectionRepository = storeInspectionRepository;
        this.partService = partService;
        this.inspectionCriterionService = inspectionCriterionService;
        this.storeVoucherTrackingService = storeVoucherTrackingService;
        this.storePartSerialService = storePartSerialService;
        this.storeStockInwardService = storeStockInwardService;
        this.returnPartsDetailService = returnPartsDetailService;
        this.storePartAvailabilityService = storePartAvailabilityService;
        this.invoiceDetailService = invoiceDetailService;
        this.serialService = serialService;
        this.demandStatusService = demandStatusService;
        this.storeInspectionGrnService = storeInspectionGrnService;
    }

    public VendorProjection findVendorByPartIdAndSerialId(Long partId, Long partSerialId){
        return storeInspectionRepository.findVendorByPartIdAndSerialId(partId, partSerialId);
    }

    /**
     * This method is responsible for create store inspection
     *
     * @param dto {@link StoreInspectionRequestDto}
     * @return successfully created message
     */
    @Transactional
    @Override
    public StoreInspection create(StoreInspectionRequestDto dto) {
        StoreInspection storeInspection = convertToEntity(dto);
        storeInspection = super.saveItem(storeInspection);
        executeStatusChangeProcedure(dto, storeInspection);
        inspectionCriterionService.saveAll(dto.getInspectionCriterionList(), storeInspection);
        return storeInspection;
    }

    /**
     * This method is responsible for update Store Inspection
     *
     * @param dto {@link StoreInspectionRequestDto}
     * @param id                        which inspection want to update
     * @return successfully updated message
     */
    @Transactional
    @Override
    public StoreInspection update(StoreInspectionRequestDto dto, Long id) {
        StoreInspection storeInspection = findByIdUnfiltered(id);
        final StoreInspection entity = updateEntity(dto, storeInspection);
        executeStatusChangeProcedure(dto, storeInspection);
        inspectionCriterionService.saveAll(dto.getInspectionCriterionList(), storeInspection);
        return super.saveItem(entity);
    }

    /**
     * Change active status
     *
     * @param id       which user want to change status
     * @param isActive boolean field
     */
    @Override
    public void updateActiveStatus(Long id, Boolean isActive) {
        StoreInspection storeInspection = findByIdUnfiltered(id);
        validateUpdatability(storeInspection);
        super.updateActiveStatus(id, isActive);
        storeInspection.setIsActive(isActive);
        saveItem(storeInspection);
    }

    /**
     * This method is responsible for search store inspection
     *
     * @param dto      {@link IdQuerySearchDto}
     * @param pageable page number
     * @return required Search result
     */
    @Override
    public PageData search(IdQuerySearchDto dto, Pageable pageable) {
        pageable = SortChanger.descendingSortByCreatedAt(pageable);
        Specification<StoreInspection> storeInspectionSpecification = buildSpecification(dto);
        Page<StoreInspection> pageData = storeInspectionRepository.findAll(storeInspectionSpecification, pageable);

        return PageData.builder()
                .model(getResponseData(pageData.getContent()))
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    /**
     * This method is responsible for get all active data
     *
     * @param isActive boolean field
     * @param pageable page number
     * @return all the active data
     */
    @Override
    public PageData getAll(Boolean isActive, Pageable pageable) {
        Page<StoreInspection> pageData = storeInspectionRepository.findAllByIsActive(isActive, pageable);

        return PageData.builder()
                .model(getResponseData(pageData.getContent()))
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    /**
     * This method is responsible for get single data by id
     *
     * @param id long type value
     * @return responsive data by given id
     */
    @Override
    public StoreInspectionResponseDto getSingle(Long id) {
        StoreInspection storeInspection = findByIdUnfiltered(id);
        StoreInspectionResponseDto storeInspectionResponseDto = getResponseData(Collections.singletonList(storeInspection)).stream().findFirst()
                .orElseThrow(() -> EngineeringManagementServerException.notFound(ErrorId.DATA_NOT_FOUND));

        convertSingleResponseDto(storeInspection, storeInspectionResponseDto);
        return storeInspectionResponseDto;
    }

    public Optional<PlanningSiProjection> getInspectionByPartIdAndSerialId(Long partId, Long serialId){
        return storeInspectionRepository.findByPartIdAndSerialIdAndIsActiveTrue(partId, serialId);
    }

    @Override
    protected StoreInspectionResponseDto convertToResponseDto(StoreInspection storeInspection) {
        return null;
    }

    @Override
    protected Specification<StoreInspection> buildSpecification(IdQuerySearchDto searchDto) {
        CustomSpecification<StoreInspection> customSpecification = new CustomSpecification<>();
        return Specification.where(customSpecification.likeSpecificationAtRoot(searchDto.getQuery(), INSPECTION_NO)
                .and(customSpecification.equalSpecificationAtChild(searchDto.getId(), RETURN_PART_DETAILS, ID)
                        .and(customSpecification.active(searchDto.getIsActive(), IS_ACTIVE_FIELD))));
    }

    @Override
    protected StoreInspection convertToEntity(StoreInspectionRequestDto dto) {

        StoreInspection storeInspection = new StoreInspection();
        String uniqueVoucherNo;

        if (Objects.isNull(dto.getInwardId())) {
            ReturnPartsDetail returnPartsDetail = returnPartsDetailService.findById(dto.getDetailsId());
            uniqueVoucherNo = storeVoucherTrackingService.generateUniqueVoucherNo(dto.getDetailsId(),
                    VoucherType.INSPECTION, returnPartsDetail.getStoreReturnPart().getStoreReturn().getVoucherNo());
        } else {
            StoreInspectionGrn storeInspectionGrn = storeInspectionGrnService.findByIdUnfiltered(dto.getStoreInspectionGrnId());
            StoreStockInward stockInward = storeStockInwardService.findById(dto.getInwardId());
            uniqueVoucherNo = storeVoucherTrackingService.generateUniqueVoucherNo(dto.getInwardId(),
                    VoucherType.INSPECTION, stockInward.getPartsInvoice().getInvoiceNo());
            dto.setGrnNo(storeInspectionGrn.getGrnNo());
            storeInspectionGrnService.setIsUsedGrnNo(dto.getStoreInspectionGrnId());
            storeInspection.setStoreInspectionGrn(storeInspectionGrn);
        }
        storeInspection.setInspectionNo(uniqueVoucherNo);



        Long partId = dto.getPartId();
        Long planningSerialId = dto.getSerialId();
        if (Objects.nonNull(planningSerialId)) {
            Optional<StorePartSerial> partSerial = storePartSerialService.
                    findByStorePartAvailabilityPartIdAndSerialIdAndIsActiveTrue(partId, planningSerialId);

            if(partSerial.isPresent()){
                storeInspection.setPartSerial(partSerial.get());
                storeInspection.setPart(partSerial.get().getStorePartAvailability().getPart());
                storeInspection.setSerialNo(partSerial.get().getSerialNumber());

                //For Updating Shelf Life and Rack Life in StorePartSerial
                partSerial.get().setSelfLife(dto.getExpireDate());
                partSerial.get().setRackLife(dto.getShelfLife());
                storePartSerialService.saveItem(partSerial.get());
            }else{
                Part part= partService.findByIdUnfiltered(dto.getPartId());
                Serial serial= serialService.findByIdUnfiltered(dto.getSerialId());
                storeInspection.setPart(part);
                dto.setSerialNo(serial.getSerialNumber());
                storePartAvailabilityService.insertAvailabilityFromInspection(part);
                generateSerial(dto, storeInspection, partId);
            }
            storeInspection.setSerial(serialService.findByIdUnfiltered(planningSerialId));
        } else {
            if (StringUtils.isBlank(dto.getSerialNo())) {
                throw EngineeringManagementServerException.badRequest(ErrorId.SERIAL_ID_AND_NO_MISSING);
            }
            generateSerial(dto, storeInspection, partId);
        }
        return populateEntity(dto, storeInspection);
    }

    @Override
    protected StoreInspection updateEntity(StoreInspectionRequestDto dto, StoreInspection storeInspection) {
        validateUpdatability(storeInspection);
        Long dtoPartId = dto.getPartId();
        boolean isNewSerial = false;
        if (!dtoPartId.equals(storeInspection.getPartId())) {
            storeInspection.setPart(partService.findById(dtoPartId));
//            if (StringUtils.isNotBlank(storeInspection.getSerialNo())) {
//              //  deletePreviousSerial(storeInspection);
//            }
        } else {
            if (StringUtils.isNotBlank(storeInspection.getSerialNo()) && !Objects.equals(storeInspection.getSerialNo(), dto.getSerialNo())) {
                //   deletePreviousSerial(storeInspection);
                if (StringUtils.isNotBlank(dto.getSerialNo())) {
                    isNewSerial = true;
                }
            }
        }
        Long planingSerialId = dto.getSerialId();
        if (Objects.nonNull(planingSerialId)) {
            if (!planingSerialId.equals(storeInspection.getPartSerialId())) {
                StorePartSerial partSerial = storePartSerialService.findByStorePartAvailabilityPartIdAndSerialIdAndIsActiveTrue(dtoPartId, planingSerialId).orElseThrow(() ->
                    EngineeringManagementServerException.badRequest(ErrorId.INVALID_STORE_PART_SERIAL));
                storeInspection.setPartSerial(partSerial);
                storeInspection.setSerial(partSerial.getSerial());
                storeInspection.setSerialNo(partSerial.getSerialNumber());

                //For Updating Shelf Life and Rack Life in StorePartSerial
                partSerial.setSelfLife(dto.getExpireDate());
                partSerial.setRackLife(dto.getShelfLife());
                storePartSerialService.saveItem(partSerial);
            }
        } else {
            if (isNewSerial) {
                generateSerial(dto, storeInspection, dtoPartId);
            }
        }
        return populateEntity(dto, storeInspection);
    }

    private void validateUpdatability(StoreInspection storeInspection) {
        if (storeInspection.getIsAlive() == Boolean.FALSE) {
            throw EngineeringManagementServerException.badRequest(ErrorId.INSPECTION_CLOSED);
        }
    }

    private boolean deletePreviousSerial(StoreInspection storeInspection) { // TODO: Work on delete flag
        try {
            storePartSerialService.deleteSerial(storeInspection.getPartId(), storeInspection.getSerialNo());
            return true;
        } catch (Exception e) {
            LOGGER.error("Could Not Delete Serial with Part: {} & Serial: {}", storeInspection.getPartId(), storeInspection.getSerialNo());
            return false;
        }
    }

    private void generateSerial(StoreInspectionRequestDto dto, StoreInspection storeInspection, Long partId) {
        StorePartSerialRequestDto partSerialRequestDto = new StorePartSerialRequestDto();
        BeanUtils.copyProperties(dto, partSerialRequestDto);
        Pair<StorePartSerial, Serial> serialPair = storePartSerialService.createNewSerial(partId, dto.getSerialNo(), partSerialRequestDto);
        storeInspection.setPartSerial(serialPair.getLeft());
        storeInspection.setSerial(serialPair.getRight());
        storeInspection.setSerialNo(dto.getSerialNo());
    }

    private StoreInspection populateEntity(StoreInspectionRequestDto dto, StoreInspection storeInspection) {
        if (Objects.nonNull(dto.getInwardId())) {
            storeInspection.setStockInward(storeStockInwardService.findByIdUnfiltered(dto.getInwardId()));
        } else {
            if (Objects.isNull(dto.getDetailsId())) {
                throw EngineeringManagementServerException.badRequest(ErrorId.RETURN_AND_INWARD_ID_MISSING);
            }
            storeInspection.setReturnPartsDetail(returnPartsDetailService.findById(dto.getDetailsId()));
        }
        storeInspection.setRemarks(dto.getRemarks());
        storeInspection.setValidUntil(dto.getValidUntil());
        storeInspection.setLotNum(dto.getLotNum());
        storeInspection.setCertiNo(dto.getCertiNo());
        storeInspection.setInspectionAuthNo(dto.getInspectionAuthNo());
        String partStateName = String.join(",", dto.getPartStateNameList());
        storeInspection.setPartStateName(partStateName);
        return storeInspection;
    }

    private VendorQuotationInvoiceDetailViewModel findUnitPrice(StoreInspection storeInspection) { // TODO: FIX THIS
        try {
            StoreStockInwardProjection stockInward = storeStockInwardService.findStockInwardById(storeInspection.getStockInwardId());
            return invoiceDetailService.getAllVendorQuotationDetailByType(stockInward.getInvoiceId(), VendorRequestType.INVOICE,
                            stockInward.getPartsInvoiceRfqType())
                    .stream().filter(model -> model.getPartId().equals(storeInspection.getPartId())).findFirst().get();
        } catch (Exception e) {
            return new VendorQuotationInvoiceDetailViewModel();
        }
    }

    private void executeStatusChangeProcedure(StoreInspectionRequestDto dto, StoreInspection storeInspection) {
        InspectionApprovalStatus dtoStatus = dto.getStatus();
        if (dtoStatus == InspectionApprovalStatus.ACCEPTED) {
            StorePartSerial partSerial = storeInspection.getPartSerial();
            partSerial.setIsActive(true);
            partSerial.setSelfLife(dto.getExpireDate());
            partSerial.setRackLife(dto.getShelfLife());
            storePartSerialService.saveItem(partSerial);
            PartAvailUpdateInternalDto partAvailUpdateInternalDto = PartAvailUpdateInternalDto.builder()
                    .parentId(storeInspection.getId())
                    .grnNo(dto.getGrnNo())
                    .transactionType(TransactionType.RECEIVE)
                    .partSerial(partSerial)
                    .quantity(getQuantity(storeInspection.getReturnPartsDetailId(),storeInspection))
                    .build();
            boolean isInward = Objects.nonNull(storeInspection.getStockInward());
            if (isInward) {
                VendorQuotationInvoiceDetailViewModel invoiceDetailViewModel = findUnitPrice(storeInspection);
                partSerial.setCurrencyId(invoiceDetailViewModel.getCurrencyId());
                partSerial.setPrice(invoiceDetailViewModel.getUnitPrice());
                partAvailUpdateInternalDto.setParentType(StorePartAvailabilityLogParentType.DEMAND);
                storeInspection.setReturnPartsDetail(saveReturnPartDetails(dto));
            } else {
                partAvailUpdateInternalDto.setParentType(StorePartAvailabilityLogParentType.RETURN);
            }
            storePartAvailabilityService.updateAvailabilityFromInspection(
                    partAvailUpdateInternalDto,
                    isInward ? storeInspection.getStockInward().getVoucherNo() :
                            storeInspection.getReturnPartsDetail().getStoreReturnPart().getStoreReturn().getVoucherNo(),
                    Objects.nonNull(storeInspection.getReturnPartsDetail().getStoreReturnPart()) ?
                            storeInspection.getReturnPartsDetail().getStoreReturnPart().getStoreReturn().getSubmittedBy().getId() :
                            storeInspection.getStockInward().getReceivedBy().getId(),
                    Objects.nonNull(storeInspection.getReturnPartsDetail().getStoreReturnPart()) ?
                            storeInspection.getReturnPartsDetail().getStoreReturnPart().getStoreReturn().getWorkFlowAction().getId() :
                            storeInspection.getStockInward().getReceivedBy().getId(),
                    Objects.nonNull(storeInspection.getReturnPartsDetail().getStoreReturnPart()) ?
                            storeInspection.getReturnPartsDetail().getStoreReturnPart().getStoreReturn().getId() :
                            storeInspection.getStockInward().getId());
            storeInspection.setIsAlive(false);
            partAvailUpdateInternalDto.setPartSerial(partSerial);

            if(Objects.isNull(dto.getId()) && isInward){
                Long piId = storeInspection.getStockInward().getPartsInvoice().getId();
                List<DemandStatus> demandStatusList = demandStatusService.findByChildIdAndVoucherTypeAndWorkFlowType(piId,VoucherType.PI, RfqType.PROCUREMENT.name());

                for(DemandStatus demandStatus : demandStatusList){
                    if(Objects.equals(dto.getPartId(), demandStatus.getPartId())){
                        demandStatusService.create(
                                dto.getPartId(),
                                storeInspection.getStockInward().getId(),
                                demandStatus.getDemandId(),
                                storeInspection.getId(),
                                dto.getQuantity(),
                                null,
                                VoucherType.INSPECTION,
                                storeInspection.getIsActive(),
                                RfqType.PROCUREMENT.name()
                        );
                    }
                }
            }
        } else {
            storeInspection.setIsAlive(true);
        }
        storeInspection.setStatus(dto.getStatus());
    }

    private Integer getQuantity(Long returnPartsDetailId,StoreInspection storeInspection) {
        if(Objects.nonNull(storeInspection.getReturnPartsDetailId())){
            StoreInspectionProjection projection =  storeInspectionRepository.findByReturnPartsDetailId(returnPartsDetailId);
            return Math.toIntExact(projection.getReturnPartsDetailStoreReturnPartQuantityReturn());
        }
        return storeInspection.getQuantity();
    }

    private ReturnPartsDetail saveReturnPartDetails(StoreInspectionRequestDto dto) {
        return returnPartsDetailService.saveFromInspection(dto);
    }

    /**
     * Custom response data method
     *
     * @return response data
     */
    private List<StoreInspectionResponseDto> getResponseData(List<StoreInspection> storeInspectionList) {
        Set<Long> inspectorIds = storeInspectionList.stream().map(StoreInspection::getId).collect(Collectors.toSet());

        Set<Long> collectionOfPartIds = storeInspectionList.stream().map(StoreInspection::getPartId).collect(Collectors.toSet());

        Map<Long, PartProjection> partProjectionMap = partService.findPartByIdIn(collectionOfPartIds).stream()
                .collect(Collectors.toMap(PartProjection::getId, Function.identity()));

        List<InspectionCriterion> inspectionCriteriontList = inspectionCriterionService.findByInspectionIdIn(inspectorIds);

        Map<Long, List<InspectionCriterionResponseDto>> detailsByInspectionCriterion = inspectionCriterionService
                .getResponse(inspectionCriteriontList).stream().collect(Collectors
                        .groupingBy(InspectionCriterionResponseDto::getInspectionId));

        return storeInspectionList.stream().map(storeInspections ->
                convertToResponseDto(storeInspections, partProjectionMap.get(storeInspections.getPartId()),
                        detailsByInspectionCriterion.get(storeInspections.getId()))).collect(Collectors.toList());
    }

    private StoreInspectionResponseDto convertToResponseDto(StoreInspection storeInspection, PartProjection partProjection,
                                                            List<InspectionCriterionResponseDto> inspectionCriterionResponseDto) {

        StoreInspectionResponseDto storeInspectionResponseDto = new StoreInspectionResponseDto();
        storeInspectionResponseDto.setId(storeInspection.getId());
        storeInspectionResponseDto.setInspectionNo(storeInspection.getInspectionNo());
        storeInspectionResponseDto.setRemarks(storeInspection.getRemarks());
        storeInspectionResponseDto.setInspectionAuthNo(storeInspection.getInspectionAuthNo());
        storeInspectionResponseDto.setLotNum(storeInspection.getLotNum());
        storeInspectionResponseDto.setCertiNo(storeInspection.getCertiNo());
        storeInspectionResponseDto.setCreatedDate(storeInspection.getCreatedAt().toLocalDate());
        storeInspectionResponseDto.setValidUntil(storeInspection.getValidUntil());
        storeInspectionResponseDto.setPartStateNameList(Objects.nonNull(storeInspection.getPartStateName())
                ? Arrays.stream(storeInspection.getPartStateName().split(",")).collect(Collectors.toList()) : Collections.EMPTY_LIST);
        if (Objects.nonNull(partProjection)) {
            storeInspectionResponseDto.setPartId(partProjection.getId());
            storeInspectionResponseDto.setPartNo(partProjection.getPartNo());
            storeInspectionResponseDto.setPartDescription(partProjection.getDescription());
        }
        StorePartSerial partSerial = storeInspection.getPartSerial();
        if (Objects.nonNull(partSerial)) {
            storeInspectionResponseDto.setShelfLife(partSerial.getRackLife());
            storeInspectionResponseDto.setExpireDate(partSerial.getSelfLife());
        }
        if (!org.springframework.util.CollectionUtils.isEmpty(inspectionCriterionResponseDto)) {
            storeInspectionResponseDto.setInspectionCriterionList(inspectionCriterionResponseDto);
        }
        return storeInspectionResponseDto;
    }

    private void convertSingleResponseDto(StoreInspection storeInspection, StoreInspectionResponseDto storeInspectionResponseDto) {
        InspectionApprovalStatus status = storeInspection.getStatus();

        storeInspectionResponseDto.setSerialNo(storeInspection.getSerialNo());
        storeInspectionResponseDto.setStatus(status);
        populateReturnDetails(storeInspectionResponseDto,storeInspection);
        storeInspectionResponseDto.setStoreStockInward(storeStockInwardService.findSerialNoById(storeInspection.getStockInwardId()));
        if (Objects.nonNull(storeInspection.getReturnPartsDetailId())) {
            storeInspectionResponseDto.setStoreReturn(returnPartsDetailService.findReturnById(storeInspection.getReturnPartsDetailId()));
        }
        storeInspectionResponseDto.setQuantity(storeInspection.getQuantity());
        StorePartSerial partSerial = storeInspection.getPartSerial();
        if (Objects.nonNull(partSerial)) {
            Serial serial = partSerial.getSerial();
            UnitMeasurement unitMeasurement = partSerial.getUnitMeasurement();
            storeInspectionResponseDto.setSerialIdNoDto(new StoreSerialIdNoDto(partSerial.getId(), serial.getId(),
                    serial.getSerialNumber(), partSerial.getPrice(), unitMeasurement.getId(), unitMeasurement.getCode()));
            storeInspectionResponseDto.setShelfLife(partSerial.getRackLife());
            storeInspectionResponseDto.setExpireDate(partSerial.getSelfLife());
            storeInspectionResponseDto.setGrnNo(partSerial.getGrnNo());
        }
    }

    private void populateReturnDetails(StoreInspectionResponseDto storeInspectionResponseDto, StoreInspection storeInspection) {
        if (Objects.isNull(storeInspection.getReturnPartsDetailId())) {
            return;
        }
        ReturnPartsDetail returnPartsDetail = returnPartsDetailService.findById(storeInspection.getReturnPartsDetailId());
        if (Objects.nonNull(returnPartsDetail)) {
            storeInspectionResponseDto.setTsn(returnPartsDetail.getTsn());
            storeInspectionResponseDto.setTso(returnPartsDetail.getTso());
            storeInspectionResponseDto.setTsr(returnPartsDetail.getTsr());
            storeInspectionResponseDto.setCso(returnPartsDetail.getCso());
            storeInspectionResponseDto.setCsr(returnPartsDetail.getCsr());
            storeInspectionResponseDto.setCsn(returnPartsDetail.getCsn());
        }
    }
}
