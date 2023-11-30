package com.digigate.engineeringmanagement.storemanagement.service.storedemand;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.constant.VoucherType;
import com.digigate.engineeringmanagement.common.entity.User;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.payload.request.search.WorkFlowDto;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.common.util.Helper;
import com.digigate.engineeringmanagement.common.util.WorkFlowUtil;
import com.digigate.engineeringmanagement.configurationmanagement.dto.projection.WorkFlowActionProjection;
import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.WorkFlowAction;
import com.digigate.engineeringmanagement.configurationmanagement.service.administration.ApprovalEmployeeService;
import com.digigate.engineeringmanagement.configurationmanagement.service.administration.WorkFlowActionService;
import com.digigate.engineeringmanagement.procurementmanagement.constant.InputType;
import com.digigate.engineeringmanagement.procurementmanagement.constant.OrderType;
import com.digigate.engineeringmanagement.procurementmanagement.constant.RfqType;
import com.digigate.engineeringmanagement.procurementmanagement.service.QuoteRequestService;
import com.digigate.engineeringmanagement.status.service.DemandStatusService;
import com.digigate.engineeringmanagement.status.serviceImpl.DemandStatusServiceImpl;
import com.digigate.engineeringmanagement.storemanagement.constant.FeatureName;
import com.digigate.engineeringmanagement.storemanagement.constant.PartAvailabilityCountType;
import com.digigate.engineeringmanagement.storemanagement.constant.RemarkType;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.*;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.PartRemark;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.GenericAttachment;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.RequisitionProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.StoreDemandProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.*;
import com.digigate.engineeringmanagement.storemanagement.payload.response.partsreceive.DashboardProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand.ApprovalStatusViewModel;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand.ProcurementRequisitionItemViewModel;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand.ProcurementRequisitionViewModel;
import com.digigate.engineeringmanagement.storemanagement.repository.storeconfiguration.ProcurementRequisitionRepository;
import com.digigate.engineeringmanagement.storemanagement.service.StoreVoucherTrackingService;
import com.digigate.engineeringmanagement.storemanagement.service.partsreceive.StoreReceiveGoodService;
import com.digigate.engineeringmanagement.storemanagement.service.storeconfiguration.PartRemarkService;
import com.digigate.engineeringmanagement.storemanagement.util.SortChanger;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.MDC;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.*;
import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.WORKFLOW_ACTION_ORDER.INITIAL_ORDER;
import static com.digigate.engineeringmanagement.common.constant.ApprovalStatusType.PROCUREMENT_REQUISITION;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

@Service
public class ProcurementRequisitionService extends AbstractSearchService<
        ProcurementRequisition,
        ProcurementRequisitionDto,
        ProcurementRequisitionCustomSearchDto> {

    private final StoreDemandService storeDemandService;
    private final StoreDemandDetailsService demandDetailsService;
    private final WorkFlowActionService workFlowActionService;
    private final ApprovalStatusService approvalStatusService;
    private final ApprovalEmployeeService approvalEmployeeService;
    private final ProcurementRequisitionRepository procurementRequisitionRepository;
    private final StoreReceiveGoodService storeReceiveGoodService;
    private final StoreVoucherTrackingService storeVoucherTrackingService;
    private final WorkFlowUtil workFlowUtil;
    private final Helper helper;
    private final QuoteRequestService quoteRequestService;
    private final PartRemarkService partRemarkService;
    private final StorePartAvailabilityService storePartAvailabilityService;
    private final ProcurementRequisitionItemService requisitionItemService;
    private final GenericAttachmentService genericAttachmentService;
    private final DemandStatusService demandStatusService;
    private final ProcurementRequisitionItemServiceImpl procurementRequisitionItemServiceImpl;
    private final DemandStatusServiceImpl demandStatusServiceImpl;

    /**
     * Constructor Parameterized
     *
     * @param procurementRequisitionRepository {@link ProcurementRequisitionRepository}
     * @param storeDemandService               {@link StoreDemandService}
     * @param demandDetailsService             {@link StoreDemandDetailsService}
     * @param workFlowActionService            {@link WorkFlowActionService}
     * @param helper                           {@link Helper}
     * @param approvalStatusService            {@link ApprovalStatusService}
     * @param approvalEmployeeService          {@link ApprovalStatusService}
     * @param workFlowUtil                     {@link WorkFlowUtil}
     * @param storeReceiveGoodService          {@link StoreReceiveGoodService}
     */
    public ProcurementRequisitionService(ProcurementRequisitionRepository procurementRequisitionRepository,
                                         @Lazy StoreDemandService storeDemandService,
                                         StoreDemandDetailsService demandDetailsService,
                                         WorkFlowActionService workFlowActionService,
                                         Helper helper,
                                         ApprovalStatusService approvalStatusService,
                                         ApprovalEmployeeService approvalEmployeeService,
                                         WorkFlowUtil workFlowUtil,
                                         @Lazy StoreReceiveGoodService storeReceiveGoodService,
                                         @Lazy QuoteRequestService quoteRequestService,
                                         StoreVoucherTrackingService storeVoucherTrackingService,
                                         @Lazy PartRemarkService partRemarkService,
                                         StorePartAvailabilityService storePartAvailabilityService,
                                         ProcurementRequisitionItemService requisitionItemService,
                                         GenericAttachmentService genericAttachmentService,
                                         DemandStatusService demandStatusService,
                                         ProcurementRequisitionItemServiceImpl procurementRequisitionItemServiceImpl,
                                         DemandStatusServiceImpl demandStatusServiceImpl) {

        super(procurementRequisitionRepository);
        this.storeDemandService = storeDemandService;
        this.procurementRequisitionRepository = procurementRequisitionRepository;
        this.demandDetailsService = demandDetailsService;
        this.workFlowActionService = workFlowActionService;
        this.helper = helper;
        this.approvalStatusService = approvalStatusService;
        this.approvalEmployeeService = approvalEmployeeService;
        this.workFlowUtil = workFlowUtil;
        this.storeReceiveGoodService = storeReceiveGoodService;
        this.quoteRequestService = quoteRequestService;
        this.storeVoucherTrackingService = storeVoucherTrackingService;
        this.partRemarkService = partRemarkService;
        this.storePartAvailabilityService = storePartAvailabilityService;
        this.requisitionItemService = requisitionItemService;
        this.genericAttachmentService = genericAttachmentService;
        this.demandStatusService = demandStatusService;
        this.procurementRequisitionItemServiceImpl = procurementRequisitionItemServiceImpl;
        this.demandStatusServiceImpl = demandStatusServiceImpl;
    }

    public ProcurementRequisition findRequisitionByPoId(Long id) {
        return procurementRequisitionRepository.findProcurementRequisitionByPartOrderId(id);
    }

    /**
     * This method is responsible for create ProcurementRequisition
     *
     * @param dto {@link ProcurementRequisitionDto}
     * @return Successfully saved message
     */
    @Transactional
    @Override
    public ProcurementRequisition create(ProcurementRequisitionDto dto) {
        validate(dto);
        List<WorkFlowAction> sortedWorkflowAction = workFlowActionService
                .getSortedWorkflowActions(Sort.Direction.ASC);
        WorkFlowAction workFlowAction = workFlowActionService
                .getByIndex(INITIAL_ORDER, sortedWorkflowAction);

        workFlowUtil.validateWorkflow(helper.getSubModuleItemId(), Collections
                .singletonList(workFlowAction.getId()));

        ProcurementRequisition procurementRequisition = convertToEntity(dto);

        procurementRequisition.setWorkFlowAction(workFlowActionService.
                getByIndex(INITIAL_ORDER + INT_ONE, sortedWorkflowAction));
        procurementRequisition = super.saveItem(procurementRequisition);

        //upload attachment
        if (!CollectionUtils.isEmpty(dto.getAttachment())) {
            genericAttachmentService.saveAllAttachments(dto.getAttachment(), FeatureName.PROCUREMENT_REQUISITION, procurementRequisition.getId());
        }

        approvalStatusService.create(ApprovalStatusDto.of(procurementRequisition.getId(), PROCUREMENT_REQUISITION, workFlowAction));
        createRequisitionItems(dto, procurementRequisition);
        return procurementRequisition;
    }

    public Pair<ProcurementRequisition,
            List<ProcurementRequisitionItem>> create(ProcurementRequisitionDto dto, OrderType orderType) {
        ProcurementRequisition procurementRequisition = convertToEntity(dto);
        procurementRequisition.setVoucherNo(INVISIBLE + ZonedDateTime.now().toInstant().toEpochMilli());
        procurementRequisition.setOrderType(orderType);

        WorkFlowAction workFlowAction = workFlowActionService.findFinalAction();
        procurementRequisition.setWorkFlowAction(workFlowAction);

        procurementRequisition = super.saveItem(procurementRequisition);

        approvalStatusService.create(ApprovalStatusDto.of(procurementRequisition.getId(), PROCUREMENT_REQUISITION, workFlowAction));
        List<ProcurementRequisitionItem> requisitionItems = createRequisitionItems(dto, procurementRequisition);

        return Pair.of(procurementRequisition, requisitionItems);
    }

    /**
     * This method is responsible for update ProcurementRequisition
     *
     * @param dto {@link ProcurementRequisitionDto}
     * @param id  which user wants to update
     * @return successfully updated message
     */
    @Transactional
    @Override
    public ProcurementRequisition update(ProcurementRequisitionDto dto, Long id) {
        ProcurementRequisition procurementRequisition = findByIdUnfiltered(id);
        Long subModuleItemId = helper.getSubModuleItemId();

        validate(dto);
        workFlowUtil.validateUpdatability(procurementRequisition.getWorkFlowActionId());

        WorkFlowAction currentAction = procurementRequisition.getWorkFlowAction();
        workFlowUtil.validateWorkflow(subModuleItemId, Arrays.asList(currentAction.getId(),
                workFlowActionService.getNavigatedAction(false, currentAction).getId()));

        ProcurementRequisition entity = updateEntity(dto, procurementRequisition);
        updateRequisitionItems(dto, procurementRequisition);
        genericAttachmentService.updateByRecordId(FeatureName.PROCUREMENT_REQUISITION, entity.getId(), dto.getAttachment());
        return super.saveItem(procurementRequisition);
    }

    public Pair<ProcurementRequisition,
            List<ProcurementRequisitionItem>> update(ProcurementRequisitionDto dto, OrderType orderType, Long poId) {
        ProcurementRequisition procurementRequisition = procurementRequisitionRepository.findProcurementRequisitionByPartOrderId(poId);
        updateEntity(dto, procurementRequisition);
        procurementRequisition.setOrderType(orderType);

        super.saveItem(procurementRequisition);

        dto.setProcurementRequisitionItemDtoList(dto.getProcurementRequisitionItemDtoList().stream().peek(item ->
                item.setRequisition(procurementRequisition)).collect(Collectors.toList()));
        List<ProcurementRequisitionItem> requisitionItems = updateRequisitionItems(dto, procurementRequisition);

        return Pair.of(procurementRequisition, requisitionItems);
    }

    /**
     * This method is responsible for active and inactive status
     *
     * @param id       which user wants to change status
     * @param isActive boolean field
     */
    @Override
    public void updateActiveStatus(Long id, Boolean isActive) {
        if (isActive == Boolean.FALSE
                && (quoteRequestService.isExistRequisitionInQuteRequest(id)
                || storeReceiveGoodService.isPossibleInactiveRequisition(id))) {
            throw new EngineeringManagementServerException(
                    ErrorId.PARENT_CAN_NOT_CHANGE_STATUS_BECAUSE_OF_CHILD_DEPENDENCY,
                    HttpStatus.PRECONDITION_FAILED,
                    MDC.get(TRACE_ID)
            );
        }
        ProcurementRequisition procurementRequisition = findByIdUnfiltered(id);
        procurementRequisition.setIsActive(isActive);
        workFlowUtil.validateUpdatability(procurementRequisition.getWorkFlowActionId());

        if (isActive == TRUE) {
            WorkFlowAction workFlowAction = workFlowActionService.getNavigatedAction(false, procurementRequisition.getWorkFlowAction());
            workFlowUtil.validateWorkflow(helper.getSubModuleItemId(), List.of(procurementRequisition.getWorkFlowActionId(), workFlowAction.getId()));
            partRemarkService.revertPreviousActionRemarks(procurementRequisition.getId(), workFlowAction, RemarkType.STORE_REQUISITION_APPROVAL_REMARK);
            procurementRequisition.setWorkFlowAction(workFlowUtil.revertAndFindPrevAction(procurementRequisition.getWorkFlowAction(), PROCUREMENT_REQUISITION,
                    procurementRequisition.getId()));
            procurementRequisition.setIsRejected(false);
            setDemandStatusIsActiveUpdatedValue(procurementRequisition);
            setDemandStatusRejectedUpdatedValue(procurementRequisition);
        }
        setDemandStatusIsActiveUpdatedValue(procurementRequisition);
        super.saveItem(procurementRequisition);

        StoreDemand storeDemand = storeDemandService.findByIdUnfiltered(procurementRequisition.getStoreDemandId());
        storeDemand.setIsRequisition(procurementRequisitionRepository.existsByStoreDemandIdAndIsActiveTrue(procurementRequisition.getStoreDemandId()));
        storeDemandService.saveItem(storeDemand);
    }

    private void setDemandStatusIsActiveUpdatedValue(ProcurementRequisition procurementRequisition) {
        List<ProcurementRequisitionItem> procurementRequisitionItems =
                procurementRequisitionItemServiceImpl.findByRequisitionId(procurementRequisition.getId());
        procurementRequisitionItems.forEach(procurementRequisitionItem -> {
            demandStatusService.updateActiveStatus(
                    procurementRequisition.getStoreDemand().getId(),
                    procurementRequisition.getId(),
                    procurementRequisitionItem.getDemandItem().getPart().getId(),
                    VoucherType.REQ,
                    procurementRequisition.getIsActive(),
                    procurementRequisition.getWorkFlowAction().getId());
        });
    }

    private void setDemandStatusRejectedUpdatedValue(ProcurementRequisition procurementRequisition) {
        List<ProcurementRequisitionItem> procurementRequisitionItems =
                procurementRequisitionItemServiceImpl.findByRequisitionId(procurementRequisition.getId());
        procurementRequisitionItems.forEach(procurementRequisitionItem -> {
            demandStatusService.updateRejectedStatus(
                    procurementRequisition.getStoreDemand().getId(),
                    procurementRequisition.getId(),
                    procurementRequisitionItem.getDemandItem().getPart().getId(),
                    VoucherType.REQ,
                    procurementRequisition.getIsRejected());
        });
    }

    /**
     * This method is responsible for searching by requisition no
     *
     * @param dto      {@link IdQuerySearchDto}
     * @param pageable {@link Pageable}
     * @return responding page data {@link PageData}
     */
    @Override
    public PageData search(ProcurementRequisitionCustomSearchDto dto, Pageable pageable) {
        pageable = SortChanger.descendingSortByCreatedAt(pageable);
        Page<ProcurementRequisition> pageData;
        List<WorkFlowActionProjection> approvedActionForUser = new ArrayList<>();
        switch (dto.getType()) {
            case PENDING:
                Set<Long> pendingSearchWorkFlowIds = workFlowUtil.findPendingWorkFlowIds(approvedActionForUser);
                if (CollectionUtils.isEmpty(pendingSearchWorkFlowIds)) {
                    pageData = Page.empty();
                    break;
                }
                pageData = customBuildSpecification(dto, pageable, pendingSearchWorkFlowIds, false);
                break;
            case APPROVED:
                Long approvedId = workFlowActionService.findFinalAction().getId();
                pageData = customBuildSpecification(dto, pageable, Collections.singleton(approvedId), null);
                break;
            case REJECTED:
                pageData = customBuildSpecification(dto, pageable, new HashSet<>(), true);
                break;
            default:
                pageData = customBuildSpecification(dto, pageable, new HashSet<>(), null);
                break;
        }
        return PageData.builder()
                .model(getResponseData(pageData.getContent(), approvedActionForUser))
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    private Page<ProcurementRequisition> customBuildSpecification(ProcurementRequisitionCustomSearchDto searchDto,
                                                                  Pageable pageable,
                                                                  Set<Long> approvedIds,
                                                                  Boolean isRejected) {
        if (FALSE == searchDto.getNotRequisition()) {
            searchDto.setNotRequisition(null);
        }
        if (searchDto.getNotIssued() == FALSE) {
            searchDto.setNotIssued(null);
        }
        CustomSpecification<ProcurementRequisition> customSpecification = new CustomSpecification<>();
        Specification<ProcurementRequisition> specification = Specification.where(
                customSpecification.equalSpecificationAtRoot(searchDto.getIsActive(), IS_ACTIVE_FIELD)
                        .and(customSpecification.equalSpecificationAtRoot(isRejected, IS_REJECTED_FIELD))
                        .and(customSpecification.inSpecificationAtRoot(approvedIds, WORKFLOW_ACTION_ID))
                        .and(customSpecification.likeSpecificationAtRoot(searchDto.getQuery(), VOUCHER_NO))
                        .and(customSpecification.likeSpecificationAtRoot(VoucherType.REQ.name(), VOUCHER_NO))
                        .and(customSpecification.equalSpecificationAtRoot(searchDto.getOrderType(), ORDER_TYPE))
                        .and(customSpecification.notEqualSpecificationAtRoot(searchDto.getNotIssued(), IS_ISSUED))
                        .and(customSpecification.notEqualSpecificationAtRoot(searchDto.getNotRequisition(), IS_REQUISITION))
                        .and(customSpecification.equalSpecificationAtRoot(searchDto.getIsAlive(), IS_ALIVE))
                        .and(customSpecification.likeSpecificationAtThirdLayerChild(searchDto.getPartNo(), PROCUREMENT_REQUISITION_ITEM, DEMAND_ITEM, PART, PART_N0))
                        .and(customSpecification.equalSpecificationAtChild(searchDto.getPriority(), PROCUREMENT_REQUISITION_ITEM, PRIORITY))
                        .and(customSpecification.likeSpecificationAtFourthEntityChild(searchDto.getAircraftName(), PROCUREMENT_REQUISITION_ITEM, DEMAND_ITEM, STORE_DEMAND, AIRCRAFT, AIRCRAFT_NAME))
                        .and(customSpecification.likeSpecificationAtFourthEntityChildAndFourthEntityJoinTypeLeft(searchDto.getDemandBy(), PROCUREMENT_REQUISITION_ITEM, DEMAND_ITEM, STORE_DEMAND, INTERNAL_DEPARTMENT, ENTITY_CODE)
                                .or(customSpecification.likeSpecificationAtFourthEntityChildAndFourthEntityJoinTypeLeft(searchDto.getDemandBy(), PROCUREMENT_REQUISITION_ITEM, DEMAND_ITEM, STORE_DEMAND, EXTERNAL_VENDOR, VENDOR)))
        );
        return procurementRequisitionRepository.findAll(specification, pageable);
    }

    /**
     * Get single data
     *
     * @param id which user want to get
     * @return data
     */
    @Override
    public ProcurementRequisitionViewModel getSingle(Long id) {
        List<WorkFlowActionProjection> approvedActionsForUser = approvalEmployeeService
                .findApprovedActionsForUser(helper.getSubModuleItemId(), Helper.getAuthUserId());

        return getResponseData(Collections.singletonList(findByIdUnfiltered(id)),
                approvedActionsForUser).stream().findFirst().orElseThrow(() ->
                EngineeringManagementServerException.notFound(ErrorId.DATA_NOT_FOUND));
    }

    /**
     * Get all method
     *
     * @param isActive boolean field
     * @param pageable page number
     * @return all the active data
     */
    @Override
    public PageData getAll(Boolean isActive, Pageable pageable) {
        Page<ProcurementRequisition> pageData = procurementRequisitionRepository.findAllByIsActive(isActive, pageable);

        return PageData.builder()
                .model(getResponseData(pageData.getContent(), Collections.emptyList()))
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    /**
     * This method is responsible for make decision for next stage
     *
     * @param id                 which user wants to make decision
     * @param approvalRequestDto ApprovalRequestDto type
     */
    @Transactional
    public void makeDecision(Long id, ApprovalRequestDto approvalRequestDto) {
        ProcurementRequisition procurementRequisition = findByIdUnfiltered(id);
        Long subModuleItemId = helper.getSubModuleItemId();

        workFlowUtil.validateUpdatability(procurementRequisition.getWorkFlowActionId());
        workFlowUtil.validateWorkflow(subModuleItemId, Collections.singletonList(
                procurementRequisition.getWorkFlowActionId()));

        if (approvalRequestDto.getApprove() == Boolean.TRUE) {
            approvalStatusService.create(ApprovalStatusDto.of(procurementRequisition.getId(), PROCUREMENT_REQUISITION,
                    procurementRequisition.getWorkFlowAction()));
            if (StringUtils.isEmpty(approvalRequestDto.getApprovalDesc())) {
                throw EngineeringManagementServerException.notFound(ErrorId.APPROVAL_REMARK_CAN_NOT_BE_EMPTY);
            }
            partRemarkService.saveApproveRemark(procurementRequisition.getId(), procurementRequisition.getWorkFlowAction().getId(),
                    RemarkType.STORE_REQUISITION_APPROVAL_REMARK, approvalRequestDto.getApprovalDesc());

            WorkFlowAction nextAction = workFlowActionService.getNavigatedAction(true, procurementRequisition.getWorkFlowAction());
            procurementRequisition.setWorkFlowAction(nextAction);
            if (procurementRequisition.getWorkFlowAction().equals(workFlowActionService.findFinalAction())) {
                updatePartAvailability(procurementRequisition.getId());
            }
        } else {
            StoreDemand storeDemand = storeDemandService.findByIdUnfiltered(procurementRequisition.getStoreDemandId());
            storeDemand.setIsRequisition(false);
            storeDemandService.saveItem(storeDemand);
            procurementRequisition.setIsRejected(true);
            if (StringUtils.isEmpty(approvalRequestDto.getRejectedDesc())) {
                throw EngineeringManagementServerException.notFound
                        (ErrorId.REJECTED_DESCRIPTION_CAN_NOT_BE_EMPTY);
            }
            procurementRequisition.setRejectedDesc(approvalRequestDto.getRejectedDesc());
        }
        ProcurementRequisition requisition = super.saveItem(procurementRequisition);
        List<ProcurementRequisitionItem> procurementRequisitionItems =
                procurementRequisitionItemServiceImpl.findByRequisitionId(id);
        procurementRequisitionItems.forEach(procurementRequisitionItem -> {
            demandStatusService.update(procurementRequisitionItem.getDemandItem().getPartId(),
                    id, requisition.getWorkFlowAction().getId(),
                    requisition.getIsRejected(),
                    VoucherType.REQ,
                    RfqType.PROCUREMENT.name());
        });
    }

    @Override
    public Boolean validateClientData(ProcurementRequisitionDto dto, Long id) {
        return super.validateClientData(dto, id);
    }

    /**
     * This method is responsible for finding requisition by id
     *
     * @param id {@link ProcurementRequisition}
     * @return responding {@link RequisitionProjection}
     */
    public RequisitionProjection findRequisitionById(Long id) {
        return procurementRequisitionRepository.findProcurementRequisitionById(id);
    }

    /**
     * This method is responsible for finding requisition by list of id
     *
     * @param idSet {@link ProcurementRequisition}
     * @return responding {@link RequisitionProjection}
     */
    public List<RequisitionProjection> findRequisitionListByIdSet(Set<Long> idSet) {
        return procurementRequisitionRepository.findProcurementRequisitionByIdIn(idSet);
    }

    /**
     * This method is responsible for checking parent
     *
     * @param id {@link StoreDemand}
     * @return responding primitive 0/1
     */
    public boolean isStoreDemandExistInRequisition(Long id) {
        return procurementRequisitionRepository.existsByStoreDemandIdAndIsActiveTrue(id);
    }

    public Map<Long, List<RequisitionProjection>> getRequisitionProjection(Set<Long> demandIds) {
        return procurementRequisitionRepository.findByStoreDemandIdInAndIsActiveTrue(demandIds).stream()
                .collect(Collectors.groupingBy(RequisitionProjection::getStoreDemandId));
    }

    @Override
    protected <T> T convertToResponseDto(ProcurementRequisition procurementRequisition) {
        return null;
    }

    @Override
    protected Specification<ProcurementRequisition> buildSpecification(ProcurementRequisitionCustomSearchDto searchDto) {
        CustomSpecification<ProcurementRequisition> customSpecification = new CustomSpecification<>();

        return Specification.where(customSpecification.likeSpecificationAtRoot(searchDto.getQuery(),
                ApplicationConstant.VOUCHER_NO));
    }

    @Override
    protected ProcurementRequisition convertToEntity(ProcurementRequisitionDto procurementRequisitionDto) {
        return populateDtoToEntity(procurementRequisitionDto, new ProcurementRequisition());
    }

    @Override
    protected ProcurementRequisition updateEntity(ProcurementRequisitionDto dto, ProcurementRequisition entity) {
        return populateDtoToEntity(dto, entity);
    }

    private void updatePartAvailability(Long requisitionId) {
        requisitionItemService.findByProcurementRequisitionId(requisitionId).forEach(requisitionItemDetails -> {
            storePartAvailabilityService.updateDemandIssuedRequisitionQuantity(requisitionItemDetails.getRequisitionQuantity(),
                    requisitionItemDetails.getDemandItemPartId(), PartAvailabilityCountType.REQUISITION);
        });
    }

    /**
     * This method is responsible for populating update and create operation
     *
     * @param procurementRequisitionDto {@link ProcurementRequisitionDto}
     * @param procurementRequisition    {@link ProcurementRequisition}
     * @return responding procurement requisition {@link ProcurementRequisition}
     */
    private ProcurementRequisition populateDtoToEntity(ProcurementRequisitionDto procurementRequisitionDto,
                                                       ProcurementRequisition procurementRequisition) {
        procurementRequisition.setSubmittedBy(User.withId(Helper.getAuthUserId()));
        procurementRequisition.setUpdateDate(LocalDate.now());
        procurementRequisition.setRemarks(procurementRequisitionDto.getRemarks());
        if (Objects.nonNull(procurementRequisitionDto.getStoreDemandId()) &&
                !procurementRequisitionDto.getStoreDemandId().equals(procurementRequisition.getStoreDemandId())) {
            StoreDemand storeDemand = storeDemandService.findByIdUnfiltered(procurementRequisitionDto.getStoreDemandId());
            storeDemand.setIsRequisition(true);
            storeDemandService.saveItem(storeDemand);
            procurementRequisition.setStoreDemand(storeDemand);
            procurementRequisition.setVoucherNo(storeVoucherTrackingService.generateUniqueVoucherNo(procurementRequisitionDto.getStoreDemandId(),
                    VoucherType.REQ, storeDemand.getVoucherNo()));
        }

        return procurementRequisition;
    }

    /**
     * This method is responsible for validating unique serial no.
     *
     * @param dto {@link ProcurementRequisitionDto}
     */
    private void validate(ProcurementRequisitionDto dto) {
        storeDemandService.findByIdAndFinalWorkflow(dto.getStoreDemandId()).orElseThrow(() ->
                EngineeringManagementServerException.badRequest(ErrorId.INVALID_OLD_DEMAND));
    }

    private List<ProcurementRequisitionItem> createRequisitionItems(ProcurementRequisitionDto dto, ProcurementRequisition requisition) {
        return dto.getProcurementRequisitionItemDtoList().stream().map(item -> {
            StoreDemandItem demandItem = (InputType.MANUAL == item.getInputType()) ? item.getStoreDemandItem()
                    : validateDemandItem(item, dto);
            ProcurementRequisitionItem requisitionItem = requisitionItemService.create(item, demandItem, requisition);
            demandStatusService.create(
                    requisitionItem.getDemandItem().getPart().getId(),
                    requisition.getStoreDemand().getId(),
                    requisition.getStoreDemand().getId(),
                    requisition.getId(),
                    requisitionItem.getRequisitionQuantity(),
                    requisition.getWorkFlowAction().getId(),
                    VoucherType.REQ,
                    requisition.getIsActive(),
                    RfqType.PROCUREMENT.name()
            );
            if (StringUtils.isNotEmpty(item.getRemark())) {
                savePartRemark(requisition.getId(), requisitionItem.getId(), item.getRemark());
            }
            return requisitionItem;
        }).collect(Collectors.toList());
    }

    private List<ProcurementRequisitionItem> updateRequisitionItems(ProcurementRequisitionDto dto,
                                                                    ProcurementRequisition requisition) {
        List<ProcurementRequisitionItem> items = new ArrayList<>();
        boolean isDeleted = true;
        for (int index = 0; index < dto.getProcurementRequisitionItemDtoList().size(); index++) {
            ProcurementRequisitionItemDto item = dto.getProcurementRequisitionItemDtoList().get(index);
            if (InputType.CS == item.getInputType()) {
                validateDemandItem(item, dto);
            }
            ProcurementRequisitionItem requisitionItem = requisitionItemService.update(item);
            if (isDeleted) {
                demandStatusServiceImpl.deleteAllDemandStatus(requisition.getStoreDemand().getId(), requisition.getId(), VoucherType.REQ);
                isDeleted = false;
            }
            demandStatusService.entityUpdate(
                    requisitionItem.getDemandItem().getPart().getId(),
                    requisition.getStoreDemand().getId(),
                    requisition.getStoreDemand().getId(),
                    requisition.getId(),
                    item.getQuantityRequested(),
                    requisition.getWorkFlowAction().getId(),
                    VoucherType.REQ,
                    requisition.getIsActive(),
                    RfqType.PROCUREMENT.name()
            );
            if (StringUtils.isNotEmpty(item.getRemark())) {
                savePartRemark(requisition.getId(), requisitionItem.getId(), item.getRemark());
            }
            items.add(requisitionItem);
        }
        return items;
    }

    private StoreDemandItem validateDemandItem(ProcurementRequisitionItemDto item, ProcurementRequisitionDto dto) {
        StoreDemandItem demandItem = demandDetailsService.findById(item.getDemandItemId());
        if (!Objects.equals(dto.getStoreDemandId(), demandItem.getStoreDemandId())) {
            throw EngineeringManagementServerException.badRequest(ErrorId.INVALID_DEMAND_ITEM);
        }
        return demandItem;
    }

    private void savePartRemark(Long parentId, Long childId, String remarks) {
        PartRemark partRemark = partRemarkService.findByItemIdAndRemarkTypeAndParentId(childId, RemarkType.PROCUREMENT_REQUISITION, parentId);
        partRemark.setRemark(remarks);
        partRemarkService.save(partRemark);
    }

    private List<ProcurementRequisitionViewModel> getResponseData(List<ProcurementRequisition> procurementRequisitions,
                                                                  List<WorkFlowActionProjection> approvedActions) {
        Set<Long> requisitionIds = procurementRequisitions.stream().map(ProcurementRequisition::getId).collect(Collectors.toSet());

        Set<Long> collectionsOfStoreDemandIds = procurementRequisitions.stream().map(ProcurementRequisition::getStoreDemandId).collect(Collectors.toSet());

        Map<Long, StoreDemandProjection> storeDemandProjectionMap = storeDemandService.findByIdIn(collectionsOfStoreDemandIds)
                .stream().collect(Collectors.toMap(StoreDemandProjection::getId, Function.identity()));

        Map<Long, List<ProcurementRequisitionItemViewModel>> requisitionItemMap = requisitionItemService.getAllResponseByViewModel(requisitionIds)
                .stream().collect(Collectors.groupingBy(ProcurementRequisitionItemViewModel::getRequisitionId));

        Map<Long, Set<String>> attachmentLinksMap = genericAttachmentService.getAllAttachmentByFeatureNameAndId(FeatureName.PROCUREMENT_REQUISITION, requisitionIds)
                .stream().collect(Collectors.groupingBy(GenericAttachment::getRecordId, Collectors.mapping(GenericAttachment::getLink, Collectors.toSet())));

        WorkFlowDto workFlowDto = workFlowUtil.prepareResponseData(requisitionIds, approvedActions, PROCUREMENT_REQUISITION);

        Map<Long, List<PartRemark>> partRemarkList = partRemarkService.findByParentIdAndRemarkType(requisitionIds, RemarkType.STORE_REQUISITION_APPROVAL_REMARK).stream()
                .collect(Collectors.groupingBy(PartRemark::getParentId)); //approval  remarks

        return procurementRequisitions
                .stream().map(procurementRequisition ->
                        convertToResponseDto(procurementRequisition,
                                attachmentLinksMap.get(procurementRequisition.getId()),
                                storeDemandProjectionMap.get(procurementRequisition.getStoreDemandId()),
                                requisitionItemMap.get(procurementRequisition.getId()),
                                workFlowDto, partRemarkList.get(procurementRequisition.getId())))
                .collect(Collectors.toList());
    }

    /**
     * This method is responsible for get all entity
     *
     * @param procurementRequisition {@link ProcurementRequisition}
     * @return responding list of procurement requisition view model {@link ProcurementRequisitionViewModel}
     */
    private ProcurementRequisitionViewModel convertToResponseDto(ProcurementRequisition procurementRequisition,
                                                                 Set<String> attachmentLinks,
                                                                 StoreDemandProjection storeDemandProjection,
                                                                 List<ProcurementRequisitionItemViewModel> requisitionItemViewModels,
                                                                 WorkFlowDto workFlowDto, List<PartRemark> partRemarks) {
        List<ApprovalStatus> approvalStatuses = workFlowDto.getStatusMap().getOrDefault(procurementRequisition.getId(), new ArrayList<>());
        Map<Long, ApprovalStatus> workFlowActionMap = approvalStatuses.stream().collect(Collectors.toMap(ApprovalStatus::getWorkFlowActionId, Function.identity()));
        WorkFlowAction workFlowAction = workFlowDto.getWorkFlowActionMap().get(procurementRequisition.getWorkFlowActionId());

        ProcurementRequisitionViewModel viewModel = new ProcurementRequisitionViewModel();

        viewModel.setId(procurementRequisition.getId());
        viewModel.setVoucherNo(procurementRequisition.getVoucherNo());
        viewModel.setRemarks(procurementRequisition.getRemarks());
        viewModel.setWorkflowName(workFlowAction.getName());
        viewModel.setWorkflowOrder(workFlowAction.getOrderNumber());
        viewModel.setActionEnabled(workFlowDto.getActionableIds().contains(procurementRequisition.getWorkFlowActionId()));
        viewModel.setEditable(workFlowDto.getEditableIds().contains(procurementRequisition.getWorkFlowActionId()));
        viewModel.setWorkFlowActionId(procurementRequisition.getWorkFlowActionId());
        viewModel.setIsRejected(procurementRequisition.getIsRejected());
        viewModel.setRejectedDesc(procurementRequisition.getRejectedDesc());
        viewModel.setAttachment(attachmentLinks);
        viewModel.setCreatedDate(procurementRequisition.getCreatedAt().toLocalDate());
        if (Objects.nonNull(storeDemandProjection)) {
            viewModel.setStoreDemandId(storeDemandProjection.getId());
            viewModel.setStoreDemandNo(storeDemandProjection.getVoucherNo());
            viewModel.setDepartmentType(storeDemandProjection.getDepartmentType());
        }
        viewModel.setRequisitionItemViewModels(requisitionItemViewModels);
        viewModel.setApprovalStatuses(approvalStatuses.stream().map(approvalStatus ->
                        ApprovalStatusViewModel.from(approvalStatus, workFlowDto.getNamesFromApprovalStatuses()))
                .collect(Collectors.toMap(ApprovalStatusViewModel::getWorkFlowActionId,
                        Function.identity(), (a, b) -> b)));
        if (CollectionUtils.isNotEmpty(partRemarks)) {
            viewModel.setApprovalRemarksResponseDtoList(partRemarks.stream().map(partRemark ->
                    partRemarkService.prepareApprovalRemarkResponse(partRemark, workFlowActionMap, workFlowDto.getNamesFromApprovalStatuses())).collect(Collectors.toList()));
        }
        return viewModel;
    }

    public List<DashboardProjection> getProcurementRequisitionData(Integer month) {
        return procurementRequisitionRepository.getProcurementRequisitionDataForMonths(month);
    }
}
