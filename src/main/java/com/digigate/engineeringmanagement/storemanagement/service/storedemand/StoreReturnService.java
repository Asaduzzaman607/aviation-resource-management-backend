package com.digigate.engineeringmanagement.storemanagement.service.storedemand;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.entity.User;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.payload.request.search.WorkFlowDto;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.service.erpDataSync.DepartmentService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.common.util.Helper;
import com.digigate.engineeringmanagement.common.util.WorkFlowUtil;
import com.digigate.engineeringmanagement.configurationmanagement.dto.projection.WorkFlowActionProjection;
import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.WorkFlowAction;
import com.digigate.engineeringmanagement.configurationmanagement.service.administration.ApprovalEmployeeService;
import com.digigate.engineeringmanagement.configurationmanagement.service.administration.WorkFlowActionService;
import com.digigate.engineeringmanagement.configurationmanagement.service.configuration.ExternalDepartmentService;
import com.digigate.engineeringmanagement.planning.constant.PartClassification;
import com.digigate.engineeringmanagement.planning.constant.StorePartAvailabilityLogParentType;
import com.digigate.engineeringmanagement.planning.service.AircraftLocationService;
import com.digigate.engineeringmanagement.storeinspector.constant.InspectionApprovalStatus;
import com.digigate.engineeringmanagement.storeinspector.payload.request.storeinspector.StoreInspectionRequestDto;
import com.digigate.engineeringmanagement.storeinspector.service.storeinspector.StoreInspectionService;
import com.digigate.engineeringmanagement.storemanagement.constant.FeatureName;
import com.digigate.engineeringmanagement.storemanagement.constant.RemarkType;
import com.digigate.engineeringmanagement.storemanagement.constant.StockRoomType;
import com.digigate.engineeringmanagement.storemanagement.constant.TransactionType;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.ApprovalStatus;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.ReturnPartsDetail;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.StoreIssue;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.PartRemark;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.GenericAttachment;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.StorePartSerial;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.StoreReturn;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.StoreReturnPart;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.StoreReturnProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.StoreReturnViewModel;
import com.digigate.engineeringmanagement.storemanagement.payload.request.PartAvailUpdateInternalDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.CommonWorkFlowSearchDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.*;
import com.digigate.engineeringmanagement.storemanagement.payload.response.OfficeIdNameDto;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand.ApprovalStatusViewModel;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand.StoreReturnPartResponseDto;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand.StoreReturnResponseDto;
import com.digigate.engineeringmanagement.storemanagement.repository.storedemand.StoreReturnRepository;
import com.digigate.engineeringmanagement.storemanagement.service.StoreVoucherTrackingService;
import com.digigate.engineeringmanagement.storemanagement.service.storeconfiguration.PartRemarkService;
import com.digigate.engineeringmanagement.storemanagement.service.storeconfiguration.StoreStockRoomService;
import com.digigate.engineeringmanagement.storemanagement.util.SortChanger;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.*;
import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.WORKFLOW_ACTION_ORDER.INITIAL_ORDER;
import static com.digigate.engineeringmanagement.common.constant.ApprovalStatusType.STORE_RETURN;
import static com.digigate.engineeringmanagement.common.constant.VoucherType.RETURN;
import static com.digigate.engineeringmanagement.storemanagement.constant.StoreReturnStatusType.COMPLETED;
import static com.digigate.engineeringmanagement.storemanagement.constant.StoreReturnStatusType.PENDING;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Collections.singleton;

@Service
public class StoreReturnService extends AbstractSearchService<StoreReturn, StoreReturnRequestDto, CommonWorkFlowSearchDto> {

    private final StoreReturnRepository storeReturnRepository;
    private final AircraftLocationService aircraftLocationService;
    private final StoreIssueService storeIssueService;
    private final DepartmentService departmentService;
    private final StoreStockRoomService storeStockRoomService;
    private final GenericAttachmentService genericAttachmentService;
    private final StoreReturnPartService storeReturnPartService;
    private final ApprovalStatusService approvalStatusService;
    private final WorkFlowActionService workFlowActionService;
    private final ApprovalEmployeeService approvalEmployeeService;
    private final StorePartAvailabilityService storePartAvailabilityService;
    private final Helper helper;
    private final WorkFlowUtil workFlowUtil;
    private final StoreVoucherTrackingService voucherTrackingService;
    private final ExternalDepartmentService externalDepartmentService;
    private final StoreInspectionService storeInspectionService;
    private final ReturnPartsDetailService returnPartsDetailService;
    private final PartRemarkService partRemarkService;

    @Autowired
    public StoreReturnService(StoreReturnRepository storeReturnRepository,
                              AircraftLocationService aircraftLocationService,
                              @Lazy StoreIssueService storeIssueService,
                              @Lazy DepartmentService departmentService,
                              @Lazy StoreStockRoomService storeStockRoomService,
                              GenericAttachmentService genericAttachmentService,
                              @Lazy StoreReturnPartService storeReturnPartService,
                              ApprovalStatusService approvalStatusService,
                              WorkFlowActionService workFlowActionService,
                              ApprovalEmployeeService approvalEmployeeService,
                              StorePartAvailabilityService storePartAvailabilityService,
                              Helper helper,
                              WorkFlowUtil workFlowUtil,
                              StoreVoucherTrackingService voucherTrackingService,
                              ExternalDepartmentService externalDepartmentService,
                              StoreInspectionService storeInspectionService,
                              ReturnPartsDetailService returnPartsDetailService,
                              PartRemarkService partRemarkService) {
        super(storeReturnRepository);
        this.departmentService = departmentService;
        this.storeStockRoomService = storeStockRoomService;
        this.storeIssueService = storeIssueService;
        this.genericAttachmentService = genericAttachmentService;
        this.aircraftLocationService = aircraftLocationService;
        this.storeReturnRepository = storeReturnRepository;
        this.storeReturnPartService = storeReturnPartService;
        this.approvalStatusService = approvalStatusService;
        this.workFlowActionService = workFlowActionService;
        this.approvalEmployeeService = approvalEmployeeService;
        this.storePartAvailabilityService = storePartAvailabilityService;
        this.helper = helper;
        this.workFlowUtil = workFlowUtil;
        this.voucherTrackingService = voucherTrackingService;
        this.externalDepartmentService = externalDepartmentService;
        this.storeInspectionService = storeInspectionService;
        this.returnPartsDetailService = returnPartsDetailService;
        this.partRemarkService = partRemarkService;
    }

    /**
     * This method is responsible for create store return
     *
     * @param storeReturnRequestDto {@link StoreReturnRequestDto}
     * @return successfully created message
     */
    @Transactional
    @Override
    public StoreReturn create(StoreReturnRequestDto storeReturnRequestDto) {
        validate(storeReturnRequestDto);
        List<WorkFlowAction> sortedWorkflowAction = workFlowActionService.getSortedWorkflowActions(Sort.Direction.ASC);

        WorkFlowAction workFlowAction = workFlowActionService.getByIndex(INITIAL_ORDER, sortedWorkflowAction);
        workFlowUtil.validateWorkflow(helper.getSubModuleItemId(), Collections.singletonList(workFlowAction.getId()));

        StoreReturn storeReturn = convertToEntity(storeReturnRequestDto);
        storeReturn.setWorkFlowAction(workFlowActionService.getByIndex(INITIAL_ORDER + INT_ONE, sortedWorkflowAction));
        Integer countFactor = 0;
        for (StoreReturnPartRequestDto storeReturnPartRequestDto : storeReturnRequestDto.getStoreReturnPartList()) {
            if (storeReturnPartRequestDto.getReturnPartsDetailDto().getIsUsed() == TRUE) {
                countFactor = storeReturnRequestDto.getStoreReturnPartList().size();
                break;
            }
        }
        storeReturn.setActivePartCount(countFactor); /** set count of return part */
        storeReturn = super.saveItem(storeReturn);
        if (CollectionUtils.isNotEmpty(storeReturnRequestDto.getAttachment())) {
            genericAttachmentService.saveAllAttachments(storeReturnRequestDto.getAttachment(),
                    FeatureName.STORE_RETURN, storeReturn.getId());
        }



        storeReturnPartService.saveAll(storeReturnRequestDto.getStoreReturnPartList(), storeReturn);
        approvalStatusService.create(ApprovalStatusDto.of(storeReturn.getId(), STORE_RETURN, workFlowAction));
        return storeReturn;
    }

    /**
     * Change active status
     *
     * @param id       which user want to change status
     * @param isActive boolean field
     */
    @Override
    public void updateActiveStatus(Long id, Boolean isActive) {
        StoreReturn storeReturn = findByIdUnfiltered(id);

        workFlowUtil.validateUpdatability(storeReturn.getWorkFlowActionId());

        if (isActive == TRUE) {
            WorkFlowAction workFlowAction = workFlowActionService.getNavigatedAction(false, storeReturn.getWorkFlowAction());
            workFlowUtil.validateWorkflow(helper.getSubModuleItemId(), List.of(storeReturn.getWorkFlowActionId(), workFlowAction.getId()));
            partRemarkService.revertPreviousActionRemarks(storeReturn.getId(), workFlowAction, RemarkType.STORE_RETURN_APPROVAL_REMARK);
            storeReturn.setWorkFlowAction(workFlowUtil.revertAndFindPrevAction(storeReturn.getWorkFlowAction(), STORE_RETURN,
                    storeReturn.getId()));
            storeReturn.setIsRejected(false);
        }
        storeReturn.setIsActive(isActive);
        saveItem(storeReturn);
    }

    /**
     * This method is responsible for update store return
     *
     * @param storeReturnRequestDto {@link StoreReturnRequestDto}
     * @param id                    which user wants to update
     * @return successfully updated message
     */
    @Transactional
    @Override
    public StoreReturn update(StoreReturnRequestDto storeReturnRequestDto, Long id) {
        validate(storeReturnRequestDto);
        StoreReturn storeReturn = findByIdUnfiltered(id);
        Long subModuleItemId = helper.getSubModuleItemId();

        validateUpdatability(storeReturn);

        WorkFlowAction currentAction = storeReturn.getWorkFlowAction();
        workFlowUtil.validateWorkflow(subModuleItemId, Arrays.asList(currentAction.getId(),
                workFlowActionService.getNavigatedAction(false, currentAction).getId()));
        StoreReturn updateStoreReturn = updateEntity(storeReturnRequestDto, storeReturn);
        if (Objects.nonNull(storeReturnRequestDto.getAttachment())) {
            genericAttachmentService.updateByRecordId(FeatureName.STORE_RETURN,
                    updateStoreReturn.getId(), storeReturnRequestDto.getAttachment());
        }
        storeReturnPartService.updateAll(storeReturnRequestDto.getStoreReturnPartList(), storeReturn);
        return super.saveItem(updateStoreReturn);
    }

    /**
     * Make decision for change approval status
     *
     * @param id                 which user want to change approval
     * @param approvalRequestDto ApprovalRequestDto type
     */
    @Transactional
    public void makeDecision(Long id, ApprovalRequestDto approvalRequestDto) {
        StoreReturn storeReturn = findByIdUnfiltered(id);
        Long subModuleItemId = helper.getSubModuleItemId();

        workFlowUtil.validateUpdatability(storeReturn.getWorkFlowActionId());
        workFlowUtil.validateWorkflow(subModuleItemId, Collections.singletonList(storeReturn.getWorkFlowActionId()));

        if (approvalRequestDto.getApprove() == Boolean.TRUE) {
            approvalStatusService.create(ApprovalStatusDto.of(storeReturn.getId(), STORE_RETURN,
                    storeReturn.getWorkFlowAction()));
            if (StringUtils.isEmpty(approvalRequestDto.getApprovalDesc())) {
                throw EngineeringManagementServerException.notFound(ErrorId.APPROVAL_REMARK_CAN_NOT_BE_EMPTY);
            }
            partRemarkService.saveApproveRemark(storeReturn.getId(), storeReturn.getWorkFlowAction().getId(),
                    RemarkType.STORE_RETURN_APPROVAL_REMARK, approvalRequestDto.getApprovalDesc());

            storeReturn.setWorkFlowAction(workFlowActionService.getNavigatedAction(true,
                    storeReturn.getWorkFlowAction()));

            if (workFlowActionService.findFinalAction().equals(storeReturn.getWorkFlowAction())) {
                executeReturnProcedure(storeReturn);
                if (storeReturn.getServiceable() == TRUE) {
                    storeReturn.setStoreReturnStatusType(COMPLETED);
                }
            }
        } else {
            storeReturn.setIsRejected(true);
            if (StringUtils.isEmpty(approvalRequestDto.getRejectedDesc())) {
                throw EngineeringManagementServerException.notFound
                        (ErrorId.REJECTED_DESCRIPTION_CAN_NOT_BE_EMPTY);
            }
            storeReturn.setRejectedDesc(approvalRequestDto.getRejectedDesc());
        }

        super.saveItem(storeReturn);
    }

    private void executeReturnProcedure(StoreReturn storeReturn) {
        List<StoreReturnPart> storeReturnPartList = storeReturnPartService.findByStoreReturnIdIn(singleton(storeReturn.getId()));
        Map<Long, List<ReturnPartsDetail>> detailsMap = returnPartsDetailService.findByStoreReturnPartIdInAndIsActiveTrue(storeReturnPartList.stream()
                        .map(StoreReturnPart::getId).collect(Collectors.toSet())).stream().collect(Collectors.groupingBy(ReturnPartsDetail::getStoreReturnPartId));

        storeReturnPartList.forEach(returnPart -> {
            List<ReturnPartsDetail> detailList = detailsMap.getOrDefault(returnPart.getId(), new ArrayList<>());
            detailList.forEach(details -> {
                if (details.getIsUsed() == TRUE) {
                    updateQuantityForUsed(returnPart, details);
                } else {
                    if (returnPart.getStoreReturn().getServiceable() == TRUE) {
                        updateQuantityForNotUsed(returnPart, details);
                    } else {
                        updateQuantityForUnserviceable(returnPart, details);
                    }
                }
            });
        });
    }

    private void updateQuantityForUsed(StoreReturnPart returnPart, ReturnPartsDetail details){
        updateQuantity(returnPart, details);
        createInspectionForUsed(returnPart,details);
        storePartAvailabilityService.updateAvailabilityForUsed(updateQuantity(returnPart, details));
    }

    private void updateQuantityForNotUsed(StoreReturnPart returnPart, ReturnPartsDetail details) {
        updateQuantity(returnPart, details);
        storePartAvailabilityService.updateAvailabilityForNotUsedReturn(updateQuantity(returnPart, details));
    }

    private void updateQuantityForUnserviceable(StoreReturnPart returnPart, ReturnPartsDetail details) {
        updateQuantity(returnPart, details);
        storePartAvailabilityService.updateAvailabilityForUnserviceableReturn(updateQuantity(returnPart, details));
    }

    private PartAvailUpdateInternalDto updateQuantity(StoreReturnPart returnPart, ReturnPartsDetail details){
        PartAvailUpdateInternalDto partAvailUpdateInternalDto = new PartAvailUpdateInternalDto();
        StorePartSerial storePartSerial = details.getRemovedPartSerial();

        partAvailUpdateInternalDto.setPartSerial(storePartSerial);
        partAvailUpdateInternalDto.setParentId(returnPart.getStoreReturnId());
        partAvailUpdateInternalDto.setVoucherNo(returnPart.getStoreReturn().getVoucherNo());
        partAvailUpdateInternalDto.setSubmittedBy(returnPart.getStoreReturn().getSubmittedById());
        partAvailUpdateInternalDto.setFinalUser(returnPart.getStoreReturn().getWorkFlowActionId());
        partAvailUpdateInternalDto.setParentType(StorePartAvailabilityLogParentType.RETURN);
        partAvailUpdateInternalDto.setGrnNo(storePartSerial.getGrnNo());
        partAvailUpdateInternalDto.setTransactionType(TransactionType.RECEIVE);
        partAvailUpdateInternalDto.setQuantity(returnPart.getPart().getClassification() == PartClassification.CONSUMABLE ?
                returnPart.getQuantityReturn().intValue() : INT_ONE);
        return partAvailUpdateInternalDto;
    }

    private void createInspectionForUsed(StoreReturnPart returnPart, ReturnPartsDetail details) {
        StoreInspectionRequestDto storeInspectionRequestDto = new StoreInspectionRequestDto();
        storeInspectionRequestDto.setPartId(returnPart.getPartId());
        storeInspectionRequestDto.setSerialId(details.getRemovedPartSerial().getSerialId());
        storeInspectionRequestDto.setDetailsId(details.getId());
        storeInspectionRequestDto.setStatus(InspectionApprovalStatus.NONE);
        storeInspectionRequestDto.setInspectionCriterionList(Collections.emptyList());
        storeInspectionRequestDto.setQuantity(returnPart.getQuantityReturn().intValue());
        storeInspectionRequestDto.setPartReturnId(returnPart.getId());
        storeInspectionRequestDto.setUomId(returnPart.getRemovedPartUomId());
        storeInspectionService.create(storeInspectionRequestDto);
    }

    /**
     * This method is responsible for search store return
     *
     * @param dto      {@link CommonWorkFlowSearchDto}
     * @param pageable page number
     * @return required result
     */
    @Override
    public PageData search(CommonWorkFlowSearchDto dto, Pageable pageable) {
        pageable = SortChanger.descendingSortByCreatedAt(pageable);
        Page<StoreReturn> pageData;
        List<WorkFlowActionProjection> approvedActionForUser = new ArrayList<>();

        switch (dto.getType()) {
            case PENDING:
                Set<Long> pendingSearchWorkFlowIds = workFlowUtil.findPendingWorkFlowIds(approvedActionForUser);
                if (org.apache.commons.collections4.CollectionUtils.isEmpty(pendingSearchWorkFlowIds)) {
                    pageData = Page.empty();
                    break;
                }
                pageData = storeReturnRepository
                        .findAllByIsRejectedFalseAndIsActiveAndWorkFlowActionIdInAndVoucherNoContains(dto.getIsActive(),
                                pendingSearchWorkFlowIds, dto.getQuery(), pageable);
                break;
            case APPROVED:
                Long approvedId = workFlowActionService.findFinalAction().getId();
                pageData = storeReturnRepository.
                        findAllByIsActiveAndWorkFlowActionIdAndVoucherNoContains(
                                dto.getIsActive(), approvedId, dto.getQuery(), pageable);
                break;
            case REJECTED:
                pageData = storeReturnRepository
                        .findAllByIsRejectedTrueAndVoucherNoContains(dto.getQuery(), pageable);
                break;
            default:
                pageData = storeReturnRepository.
                        findAllByIsActiveAndVoucherNoContains(dto.getIsActive(), dto.getQuery(),
                                pageable);
                break;
        }
        return PageData.builder()
                .model(getResponseData(pageData.getContent(), approvedActionForUser))
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    /**
     * This method is responsible for get all
     *
     * @param isActive boolean field
     * @param pageable page number
     * @return all the active data
     */
    @Override
    public PageData getAll(Boolean isActive, Pageable pageable) {
        Page<StoreReturn> pageData = storeReturnRepository.findAllByIsActive(isActive, pageable);

        return PageData.builder()
                .model(getResponseData(pageData.getContent(), Collections.emptyList()))
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    public boolean existsByLocationIdAndIsActiveTrue(Long id) {
        return storeReturnRepository.existsByLocationIdAndIsActiveTrue(id);
    }

    public boolean existsByRoomIdAndIsActiveTrue(Long id) {
        return storeReturnRepository.existsByStoreStockRoomIdAndIsActiveTrue(id);
    }

    public boolean existsByDepartmentIdAndIsActiveTrue(Long id) {
        return storeReturnRepository.existsByDepartmentIdAndIsActiveTrue(id);
    }

    public List<StoreReturnProjection> findByIdIn(Set<Long> ids) {
        return storeReturnRepository.findByIdIn(ids);
    }

    /**
     * This method is responsible for get single data by id
     *
     * @param id long type value
     * @return responsive data
     */
    @Override
    public StoreReturnResponseDto getSingle(Long id) {
        List<WorkFlowActionProjection> approvedActionsForUser = approvalEmployeeService
                .findApprovedActionsForUser(helper.getSubModuleItemId(), Helper.getAuthUserId());

        StoreReturnResponseDto storeReturnResponseDto = getResponseData(Collections.singletonList(findByIdUnfiltered(id)),
                approvedActionsForUser).stream().findFirst().orElseThrow(() ->
                EngineeringManagementServerException.notFound(ErrorId.DATA_NOT_FOUND));
        storeReturnResponseDto.setAttachment(genericAttachmentService.getLinksByFeatureNameAndId(FeatureName.STORE_RETURN,
                singleton(storeReturnResponseDto.getId())));
        return storeReturnResponseDto;
    }

    @Override
    protected StoreReturnResponseDto convertToResponseDto(StoreReturn returnUnusualParts) {
        return null;
    }

    @Override
    protected StoreReturn convertToEntity(StoreReturnRequestDto storeReturnRequestDto) {
        return populateEntity(storeReturnRequestDto, new StoreReturn());
    }

    @Override
    protected StoreReturn updateEntity(StoreReturnRequestDto dto, StoreReturn entity) {
        return populateEntity(dto, entity);
    }

    @Override
    protected Specification<StoreReturn> buildSpecification(CommonWorkFlowSearchDto searchDto) {
        CustomSpecification<StoreReturn> customSpecification = new CustomSpecification<>();

        return Specification.where(customSpecification.likeSpecificationAtRoot(searchDto.getQuery(),
                ApplicationConstant.VOUCHER_NO));
    }

    private StoreReturn populateEntity(StoreReturnRequestDto dto, StoreReturn storeReturn) {
        if (dto.getIsServiceable() == FALSE) {
            storeReturn.setUnserviceableStatus(dto.getUnserviceableStatus());
            storeReturn.setStoreLocation(dto.getStoreLocation());
        }
        if (dto.getIsServiceable() == TRUE) {
            storeReturn.setStoreReturnStatusType(PENDING);
        }
        storeReturn.setAircraftRegistration(dto.getAircraftRegistration());
        storeReturn.setStockRoomType(dto.getStockRoomType());
        storeReturn.setPartClassification(dto.getPartClassification());
        storeReturn.setLocation(aircraftLocationService.findById(dto.getLocationId()));
        storeReturn.setSubmittedBy(User.withId(Helper.getAuthUserId()));
        if (Objects.isNull(dto.getId())) {
            String voucherNo;
            if (Objects.equals(dto.getIsServiceable(), TRUE)) {
                if (Objects.nonNull(dto.getStoreIssueId())) {
                    StoreIssue storeIssue = storeIssueService.findByIdUnfiltered(dto.getStoreIssueId());
                    /** set store issue voucher for Serviceable **/
                    storeReturn.setStoreIssue(storeIssue);
                    voucherNo = generateServiceableVoucher(voucherTrackingService
                            .generateUniqueVoucherNo(dto.getStoreIssueId(), RETURN, storeIssue.getVoucherNo()));
                } else {
                    voucherNo = generateServiceableVoucher(voucherTrackingService.generateUniqueNo(RETURN));
                }
            } else {
                if (Objects.nonNull(dto.getStoreIssueId())) {
                    StoreIssue storeIssue = storeIssueService.findByIdUnfiltered(dto.getStoreIssueId());
                    /** set store issue voucher for Unserviceable **/
                    storeReturn.setStoreIssue(storeIssue);
                    voucherNo = generateUnserviceableVoucher(voucherTrackingService
                            .generateUniqueVoucherNo(dto.getStoreIssueId(), RETURN, storeIssue.getVoucherNo()));
                } else {
                    voucherNo = generateUnserviceableVoucher(voucherTrackingService.generateUniqueNo(RETURN));
                }
            }
            storeReturn.setVoucherNo(voucherNo);
            storeReturn.setServiceable(dto.getIsServiceable());
        }
        if (Objects.nonNull(dto.getStoreStockRoomId())) {
            storeReturn.setStoreStockRoom(storeStockRoomService.findByIdUnfiltered(dto.getStoreStockRoomId()));
        }
        storeReturn.setRemarks(dto.getRemarks());
        storeReturn.setUpdateDate(LocalDate.now());// set localDate Time Now
        storeReturn.setIsInternalDept(dto.getIsInternalDept());
        storeReturn.setWorkOrderNumber(dto.getWorkOrderNumber());
        storeReturn.setWorkOrderSerial(dto.getWorkOrderSerial());
        if (dto.getIsInternalDept() == TRUE) {
            storeReturn.setDepartment(departmentService.findById(dto.getDepartmentId()));
        } else {
            storeReturn.setVendor(externalDepartmentService.findById(dto.getDepartmentId()));
        }
        return storeReturn;
    }

    /**
     * Custom get all method
     *
     * @param storeReturns boolean field
     * @return data
     */
    private List<StoreReturnResponseDto> getResponseData(List<StoreReturn> storeReturns,
                                                         List<WorkFlowActionProjection> approvedActions) {

        Set<Long> returnIds = storeReturns.stream().map(StoreReturn::getId).collect(Collectors.toSet());
        Map<Long, StoreReturnViewModel> returnViewModels = storeReturnRepository
                .findJoinedProjectionByIdIn(returnIds).stream()
                .collect(Collectors.toMap(StoreReturnViewModel::getId, Function.identity()));

        List<StoreReturnPart> storeReturnPartList = storeReturnPartService.findByStoreReturnIdIn(returnIds);

        Map<Long, List<StoreReturnPartResponseDto>> detailsByStoreReturnParts = storeReturnPartService
                .getResponse(storeReturnPartList).stream()
                .collect(Collectors.groupingBy(StoreReturnPartResponseDto::getStoreReturnId));

        Map<Long, Set<String>> attachmentLinksMap = genericAttachmentService
                .getAllAttachmentByFeatureNameAndId(FeatureName.STORE_RETURN, returnIds)
                .stream().collect(Collectors.groupingBy(GenericAttachment::getRecordId,
                        Collectors.mapping(GenericAttachment::getLink, Collectors.toSet())));

        WorkFlowDto workFlowDto = workFlowUtil.prepareResponseData(returnIds, approvedActions, STORE_RETURN);

        Map<Long, List<PartRemark>> partRemarkList = partRemarkService.findByParentIdAndRemarkType(returnIds, RemarkType.STORE_RETURN_APPROVAL_REMARK).stream()
                .collect(Collectors.groupingBy(PartRemark::getParentId)); //approval  remarks
        return storeReturns
                .stream()
                .map(storeReturn ->
                        convertToResponseDto(
                                storeReturn,
                                attachmentLinksMap.get(storeReturn.getId()),
                                returnViewModels.getOrDefault(storeReturn.getId(), new StoreReturnViewModel()),
                                detailsByStoreReturnParts.get(storeReturn.getId()),
                                workFlowDto, partRemarkList.get(storeReturn.getId())
                        ))
                .collect(Collectors.toList());
    }

    private StoreReturnResponseDto convertToResponseDto(StoreReturn storeReturn, Set<String> attachmentLinks,
                                                        StoreReturnViewModel storeReturnViewModel,
                                                        List<StoreReturnPartResponseDto> storeReturnPart,
                                                        WorkFlowDto workFlowDto, List<PartRemark> partRemarks) {

        List<ApprovalStatus> approvalStatuses = workFlowDto.getStatusMap().getOrDefault(storeReturn.getId(), new ArrayList<>());
        Map<Long, ApprovalStatus> workFlowActionMap = approvalStatuses.stream().collect(Collectors.toMap(ApprovalStatus::getWorkFlowActionId, Function.identity()));
        WorkFlowAction workFlowAction = workFlowDto.getWorkFlowActionMap().get(storeReturn.getWorkFlowActionId());

        StoreReturnResponseDto storeReturnResponseDto = new StoreReturnResponseDto();
        storeReturnResponseDto.setId(storeReturn.getId());
        storeReturnResponseDto.setSubmittedById(storeReturn.getSubmittedById());
        storeReturnResponseDto.setAircraftRegistration(storeReturn.getAircraftRegistration());
        storeReturnResponseDto.setRemarks(storeReturn.getRemarks());
        storeReturnResponseDto.setAttachment(attachmentLinks);
        storeReturnResponseDto.setVoucherNo(storeReturn.getVoucherNo());
        storeReturnResponseDto.setIsActive(storeReturn.getIsActive());
        storeReturnResponseDto.setWorkflowName(workFlowAction.getName());
        storeReturnResponseDto.setWorkflowOrder(workFlowAction.getOrderNumber());
        storeReturnResponseDto.setActionEnabled(workFlowDto.getActionableIds().contains(storeReturn.getWorkFlowActionId()));
        storeReturnResponseDto.setEditable(workFlowDto.getEditableIds().contains(storeReturn.getWorkFlowActionId()));
        storeReturnResponseDto.setWorkFlowActionId(storeReturn.getWorkFlowActionId());
        storeReturnResponseDto.setLocationId(storeReturnViewModel.getLocationId());
        storeReturnResponseDto.setLocationCode(storeReturnViewModel.getLocationCode());
        storeReturnResponseDto.setStoreStockRoomId(storeReturnViewModel.getStockRoomId());
        storeReturnResponseDto.setStockRoomName(storeReturnViewModel.getStockRoomName());
        storeReturnResponseDto.setStoreLocation(storeReturnViewModel.getStoreLocation());
        storeReturnResponseDto.setWorkOrderNumber(storeReturn.getWorkOrderNumber());
        storeReturnResponseDto.setWorkOrderSerial(storeReturn.getWorkOrderSerial());
        if (Objects.nonNull(storeReturn.getStockRoomType())) {
            storeReturnResponseDto.setStockRoomType((StockRoomType.customStockRoomType(storeReturn.getStockRoomType())));
        }
        if (Objects.nonNull(storeReturn.getPartClassification())) {
            storeReturnResponseDto.setPartClassification(storeReturn.getPartClassification().getId());
        }
        if (Objects.nonNull(storeReturn.getStoreReturnStatusType())) {
            storeReturnResponseDto.setStoreReturnStatusType(String.valueOf(storeReturn.getStoreReturnStatusType()));
        }
        if (Objects.nonNull(storeReturn.getUnserviceableStatus())) {
            storeReturnResponseDto.setStoreReturnStatusType(storeReturn.getUnserviceableStatus());
        }
        if (Objects.nonNull(storeReturnViewModel.getStockRoomId())) {
            OfficeIdNameDto officeIdNameDto = storeReturnRepository.findOfficeCodeDependingNoRoom(storeReturn.getId());
            storeReturnResponseDto.setOfficeId(officeIdNameDto.getOfficeId());
            storeReturnResponseDto.setOfficeCode(officeIdNameDto.getOfficeCode());
        }
        storeReturnResponseDto.setStoreIssueId(storeReturnViewModel.getStoreIssueId());
        storeReturnResponseDto.setStoreIssueVoucherNo(storeReturnViewModel.getStoreIssueVoucherNo());
        storeReturnResponseDto.setReturningOfficerId(storeReturnViewModel.getReturningOfficerId());
        storeReturnResponseDto.setSubmittedById(storeReturnViewModel.getSubmittedById());
        storeReturnResponseDto.setIsRejected(storeReturn.getIsRejected());
        storeReturnResponseDto.setRejectedDesc(storeReturn.getRejectedDesc());
        storeReturnResponseDto.setStoreReturnPartList(storeReturnPart);
        storeReturnResponseDto.setApprovalStatuses(approvalStatuses.stream().map(approvalStatus ->
                        ApprovalStatusViewModel.from(approvalStatus, workFlowDto.getNamesFromApprovalStatuses()))
                .collect(Collectors.toMap(ApprovalStatusViewModel::getWorkFlowActionId,
                        Function.identity(), (a, b) -> b)));
        if (CollectionUtils.isNotEmpty(partRemarks)) {
            storeReturnResponseDto.setApprovalRemarksResponseDtoList(partRemarks.stream().map(partRemark ->
                    partRemarkService.prepareApprovalRemarkResponse(partRemark, workFlowActionMap, workFlowDto.getNamesFromApprovalStatuses())).collect(Collectors.toList()));
        }
        storeReturnResponseDto.setIsInternalDept(storeReturn.getIsInternalDept());

        if (storeReturnResponseDto.getIsInternalDept() == TRUE) {
            storeReturnResponseDto.setDepartmentId(storeReturnViewModel.getDepartmentId());
            storeReturnResponseDto.setDepartmentName(storeReturnViewModel.getDepartmentName());
        } else {
            storeReturnResponseDto.setDepartmentId(storeReturnViewModel.getExternalDepartmentId());
            storeReturnResponseDto.setDepartmentName(storeReturnViewModel.getExternalDepartmentName());
        }
        storeReturnResponseDto.setIsServiceable(storeReturn.getServiceable());
        storeReturnResponseDto.setCreateDate(storeReturn.getCreatedAt().toLocalDate());
        storeReturnResponseDto.setPartViewModels(storeReturnRepository.findPartOrderName(storeReturn.getId()));
        /** setting active part count status */
        storeReturnResponseDto.setIsActiveCountZero(isActiveCountZero(storeReturn));
        return storeReturnResponseDto;
    }

    private Boolean isActiveCountZero(StoreReturn storeReturn) {
        Integer partCount = Objects.nonNull(storeReturn.getActivePartCount()) ?
                storeReturn.getActivePartCount() : VALUE_ZERO;
        return partCount <= VALUE_ZERO;
    }

    private void validateUpdatability(StoreReturn storeReturn) {
        if (storeReturn.getWorkFlowActionId().equals(workFlowActionService.findFinalAction().getId())) {
            throw EngineeringManagementServerException.badRequest(ErrorId.ALREADY_APPROVED);
        }
    }

    /**
     * Thi method check part serialis removed or installed
     *
     * @param storeReturnRequestDto {@link StoreReturnRequestDto}
     */
    private void validate(StoreReturnRequestDto storeReturnRequestDto) {
        List<StoreReturnPartRequestDto> storeReturnPartList = storeReturnRequestDto.getStoreReturnPartList();
        storeReturnPartList.forEach(dto -> {
            ReturnPartsDetailDto returnPartsDetailDto = dto.getReturnPartsDetailDto();

            if(Objects.equals(storeReturnRequestDto.getIsServiceable(), TRUE)) {
                if (Objects.isNull(returnPartsDetailDto.getRemovedPlanningSerialId())) {
                    throw EngineeringManagementServerException.badRequest(ErrorId.INVALID_REMOVED_SERIAL_ID);
                }
            } else {
                if (Objects.isNull(returnPartsDetailDto.getInstalledPlanningSerialId()) || Objects.isNull(returnPartsDetailDto.getRemovedPlanningSerialId())) {
                    throw EngineeringManagementServerException.badRequest
                            (ErrorId.SERIAL_NOS_SHOULD_BE_NOT_EMPTY);
                }
            }
            if (Objects.equals(returnPartsDetailDto.getInstalledPlanningSerialId(), returnPartsDetailDto.getRemovedPlanningSerialId())) {
                throw EngineeringManagementServerException.badRequest
                        (ErrorId.INSTALL_AND_REMOVED_PART_SERIAL_ID_CAN_NOT_BE_SAME);
            }
        });
    }

    private String generateUnserviceableVoucher(String postFix) {
        return UNSERVICEABLE + SLASH + postFix;
    }

    private String generateServiceableVoucher(String postFix) {
        return SERVICEABLE + SLASH + postFix;
    }
}
