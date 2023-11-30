package com.digigate.engineeringmanagement.storemanagement.service.storedemand;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.constant.VoucherType;
import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.payload.request.search.WorkFlowDto;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.common.util.Helper;
import com.digigate.engineeringmanagement.common.util.WorkFlowUtil;
import com.digigate.engineeringmanagement.configurationmanagement.dto.projection.WorkFlowActionProjection;
import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.WorkFlowAction;
import com.digigate.engineeringmanagement.configurationmanagement.service.administration.WorkFlowActionService;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.ApprovalStatus;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.StoreWorkOrder;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.CommonWorkFlowSearchDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.ApprovalRequestDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.ApprovalStatusDto;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand.ApprovalStatusViewModel;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand.StoreWorkOrderDto;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand.StoreWorkOrderResponse;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand.WorkOrderComponent;
import com.digigate.engineeringmanagement.storemanagement.repository.storedemand.StoreWorkOrderRepository;
import com.digigate.engineeringmanagement.storemanagement.service.StoreVoucherTrackingService;
import com.digigate.engineeringmanagement.storemanagement.util.SortChanger;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.INT_ONE;
import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.WORKFLOW_ACTION_ORDER.INITIAL_ORDER;
import static com.digigate.engineeringmanagement.common.constant.ApprovalStatusType.STORE_WORK_ORDER;
import static java.lang.Boolean.TRUE;

/**
 * Store Work Order Service
 *
 * @author Sayem Hasnat
 */
@Service
public class StoreWorkOrderService extends AbstractSearchService<StoreWorkOrder, StoreWorkOrderDto, CommonWorkFlowSearchDto> {


    private final ReturnPartsDetailService unserviceablePartService;
    private final StoreWorkOrderRepository storeWorkOrderRepository;
    private final ApprovalStatusService approvalStatusService;
    private final WorkFlowActionService workFlowActionService;
    private final WorkFlowUtil workFlowUtil;
    private final Helper helper;
    private final StoreVoucherTrackingService voucherTrackingService;

    public StoreWorkOrderService(StoreWorkOrderRepository storeWorkOrderRepository,
                                 ReturnPartsDetailService unserviceablePartService,
                                 ApprovalStatusService approvalStatusService,
                                 WorkFlowActionService workFlowActionService,
                                 WorkFlowUtil workFlowUtil, Helper helper,
                                 StoreVoucherTrackingService voucherTrackingService) {
        super(storeWorkOrderRepository);
        this.unserviceablePartService = unserviceablePartService;
        this.storeWorkOrderRepository = storeWorkOrderRepository;
        this.approvalStatusService = approvalStatusService;
        this.workFlowActionService = workFlowActionService;
        this.workFlowUtil = workFlowUtil;
        this.helper = helper;
        this.voucherTrackingService = voucherTrackingService;
    }

    /**
     * this method is responsible for save Store work order
     *
     * @param storeWorkOrderDto {@link StoreWorkOrderDto}
     * @return {@link StoreWorkOrder}
     */
    @Override
    public StoreWorkOrder create(StoreWorkOrderDto storeWorkOrderDto) {
        List<WorkFlowAction> sortedWorkflowAction = workFlowActionService.getSortedWorkflowActions(Sort.Direction.ASC);

        WorkFlowAction workFlowAction = workFlowActionService.getByIndex(INITIAL_ORDER, sortedWorkflowAction);

        workFlowUtil.validateWorkflow(helper.getSubModuleItemId(), Collections.singletonList(workFlowAction.getId()));

        StoreWorkOrder storeWorkOrder = convertToEntity(storeWorkOrderDto);
        storeWorkOrder.setWorkFlowAction(workFlowActionService.getByIndex(INITIAL_ORDER + INT_ONE, sortedWorkflowAction));
        storeWorkOrder = super.saveItem(storeWorkOrder);
        approvalStatusService.create(ApprovalStatusDto.of(storeWorkOrder.getId(), STORE_WORK_ORDER, workFlowAction));
        return storeWorkOrder;
    }

    /**
     * This method is responsible for update Store Work Order
     *
     * @param storeWorkOrderDto {@link StoreWorkOrderDto}
     * @param id                {@link Long}
     * @return {@link StoreWorkOrder}
     */
    @Override
    public StoreWorkOrder update(StoreWorkOrderDto storeWorkOrderDto, Long id) {
        StoreWorkOrder storeWorkOrder = findByIdUnfiltered(id);
        Long subModuleItemId = helper.getSubModuleItemId();

        validateUpdatability(storeWorkOrder);
        WorkFlowAction currentAction = storeWorkOrder.getWorkFlowAction();
        workFlowUtil.validateWorkflow(subModuleItemId, Arrays.asList(currentAction.getId(),
                workFlowActionService.getNavigatedAction(false, currentAction).getId()));

        StoreWorkOrder updateStoreWorkOrder = updateEntity(storeWorkOrderDto, storeWorkOrder);
        return super.saveItem(updateStoreWorkOrder);
    }

    private void validateUpdatability(StoreWorkOrder storeWorkOrder) {
        if (storeWorkOrder.getWorkFlowActionId().equals(workFlowActionService.findFinalAction().getId())) {
            throw EngineeringManagementServerException.badRequest(ErrorId.ALREADY_APPROVED);
        }
    }

    /**
     * This method will convert Entity to Response
     *
     * @param storeWorkOrder {@link StoreWorkOrder}
     * @return {@link StoreWorkOrderResponse}
     */
    @Override
    protected StoreWorkOrderResponse convertToResponseDto(StoreWorkOrder storeWorkOrder) {
        StoreWorkOrderResponse storeWorkOrderResponse = new StoreWorkOrderResponse();
        storeWorkOrderResponse.setId(storeWorkOrder.getId());
        storeWorkOrderResponse.setWorkOrderNo(storeWorkOrder.getWorkOrderNo());
        storeWorkOrderResponse.setUnserviceablePartId(storeWorkOrder.getUnserviceablePartId());
        storeWorkOrderResponse.setReasonRemark(storeWorkOrder.getReasonRemark());
        storeWorkOrderResponse.setUpdateDate(storeWorkOrder.getUpdateDate());
        return storeWorkOrderResponse;
    }

    /**
     * This method will convert dto to entity
     *
     * @param storeWorkOrderDto {@link StoreWorkOrderDto}
     * @return {@link StoreWorkOrder}
     */
    @Override
    protected StoreWorkOrder convertToEntity(StoreWorkOrderDto storeWorkOrderDto) {
        return prepareEntity(storeWorkOrderDto, new StoreWorkOrder());
    }

    /**
     * This method will convert entity for update
     *
     * @param storeWorkOrderDto {@link StoreWorkOrderDto}
     * @param storeWorkOrder    {@link StoreWorkOrder}
     * @return {@link StoreWorkOrder}
     */
    @Override
    protected StoreWorkOrder updateEntity(StoreWorkOrderDto storeWorkOrderDto, StoreWorkOrder storeWorkOrder) {

        return prepareEntity(storeWorkOrderDto, storeWorkOrder);
    }

    /**
     * this method will search common workflow with query
     * @param searchDto     {@link CommonWorkFlowSearchDto}
     * @param pageable      {@link Pageable}
     * @return              {@link PageData}
     */
    @Override
    public PageData search(CommonWorkFlowSearchDto searchDto, Pageable pageable) {
        pageable = SortChanger.descendingSortByCreatedAt(pageable);
        Page<StoreWorkOrder> pageData;
        List<WorkFlowActionProjection> approvedActionForUser = new ArrayList<>();

        switch (searchDto.getType()) {
            case PENDING:
                Set<Long> pendingSearchWorkFlowIds = workFlowUtil.findPendingWorkFlowIds(approvedActionForUser);
                if (CollectionUtils.isEmpty(pendingSearchWorkFlowIds)) {
                    pageData = Page.empty();
                    break;
                }
                pageData = storeWorkOrderRepository
                        .findAllByIsRejectedFalseAndIsActiveAndWorkFlowActionIdInAndWorkOrderNoContains(searchDto.getIsActive(),
                                pendingSearchWorkFlowIds, searchDto.getQuery(), pageable);
                break;
            case APPROVED:
                Long approvedId = workFlowActionService.findFinalAction().getId();
                pageData = storeWorkOrderRepository.findAllByIsActiveAndWorkFlowActionIdAndWorkOrderNoContains(
                        searchDto.getIsActive(), approvedId, searchDto.getQuery(), pageable);
                break;
            case REJECTED:
                pageData = storeWorkOrderRepository.findAllByIsRejectedTrueAndWorkOrderNoContains(searchDto.getQuery(), pageable);
                break;
            default:
                pageData = storeWorkOrderRepository.
                        findAllByIsActiveAndWorkOrderNoContains(searchDto.getIsActive(), searchDto.getQuery(), pageable);
                break;
        }
        return PageData.builder()
                .model(getResponseData(pageData.getContent(), approvedActionForUser))
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    private List<StoreWorkOrderResponse> getResponseData(List<StoreWorkOrder> storeWorkOrderList,
                                                         List<WorkFlowActionProjection> approvedActionForUser) {
        Set<Long> storeWorkOrderIds = storeWorkOrderList.stream()
                .map(AbstractDomainBasedEntity::getId).collect(Collectors.toSet());
        WorkFlowDto workFlowDto = workFlowUtil.prepareResponseData(storeWorkOrderIds, approvedActionForUser, STORE_WORK_ORDER);
        return convertToResponseDtoWithWorkFlow(storeWorkOrderList,workFlowDto);
    }

    private List<StoreWorkOrderResponse> convertToResponseDtoWithWorkFlow(List<StoreWorkOrder> storeWorkOrderList, WorkFlowDto workFlowDto) {
        List<StoreWorkOrderResponse> storeWorkOrderResponseList = new ArrayList<>();
        for (StoreWorkOrder storeWorkOrder : storeWorkOrderList) {
            StoreWorkOrderResponse storeWorkOrderResponse = new StoreWorkOrderResponse();
            storeWorkOrderResponse.setId(storeWorkOrder.getId());
            storeWorkOrderResponse.setWorkOrderNo(storeWorkOrder.getWorkOrderNo());
            storeWorkOrderResponse.setUpdateDate(storeWorkOrder.getUpdateDate());
            WorkFlowAction workFlowAction = workFlowDto.getWorkFlowActionMap().get(storeWorkOrder.getWorkFlowActionId());
            storeWorkOrderResponse.setWorkflowName(workFlowAction.getName());
            storeWorkOrderResponse.setWorkflowOrder(workFlowAction.getOrderNumber());
            storeWorkOrderResponse.setIsRejected(storeWorkOrder.getIsRejected());
            storeWorkOrderResponse.setRejectedDesc(storeWorkOrder.getRejectedDesc());
            storeWorkOrderResponse.setActionEnabled(workFlowDto.getActionableIds().contains(storeWorkOrder.getWorkFlowActionId()));
            storeWorkOrderResponse.setEditable(workFlowDto.getEditableIds().contains(storeWorkOrder.getWorkFlowActionId()));
            storeWorkOrderResponse.setWorkFlowActionId(storeWorkOrder.getWorkFlowActionId());
            List<ApprovalStatus> approvalStatuses = workFlowDto.getStatusMap().getOrDefault(storeWorkOrder.getId(), new ArrayList<>());
            storeWorkOrderResponse.setApprovalStatuses(approvalStatuses.stream().map(approvalStatus ->
                            ApprovalStatusViewModel.from(approvalStatus, workFlowDto.getNamesFromApprovalStatuses()))
                    .collect(Collectors.toMap(ApprovalStatusViewModel::getWorkFlowActionId,
                            Function.identity(), (a, b) -> b)));
            storeWorkOrderResponseList.add(storeWorkOrderResponse);
        }
        return storeWorkOrderResponseList;
    }

    /**
     * This method responsible for search
     *
     * @param searchDto {@link IdQuerySearchDto}
     */
    @Override
    protected Specification<StoreWorkOrder> buildSpecification(CommonWorkFlowSearchDto searchDto) {
        CustomSpecification<StoreWorkOrder> customSpecification = new CustomSpecification<>();
        return Specification.where(
                customSpecification.likeSpecificationAtRoot(searchDto.getQuery(), "workOrderNo")
        ).and(new CustomSpecification<StoreWorkOrder>()
                .active(searchDto.getIsActive(), ApplicationConstant.IS_ACTIVE_FIELD));
    }

    /**
     * This method responsible to  prepare entity
     *
     * @param storeWorkOrderDto {@link StoreWorkOrderDto}
     * @param storeWorkOrder    {@link StoreWorkOrder}
     * @return                  {@link StoreWorkOrder}
     */
    protected StoreWorkOrder prepareEntity(StoreWorkOrderDto storeWorkOrderDto, StoreWorkOrder storeWorkOrder) {
        storeWorkOrder.setChkSvcOhrnNo(storeWorkOrderDto.getChkSvcOhrnNo());
        if (Objects.nonNull(storeWorkOrderDto.getUnserviceablePartId())) {
            storeWorkOrder.setUnserviceablePart(
                    unserviceablePartService.findById(storeWorkOrderDto.getUnserviceablePartId()));
        }
        storeWorkOrder.setReasonRemark(storeWorkOrderDto.getReasonRemark());
        storeWorkOrder.setUpdateDate(LocalDate.now());
        if (Objects.isNull(storeWorkOrder.getWorkOrderNo())) {
            Long workOrderId = super.saveItem(storeWorkOrder).getId();
            storeWorkOrder.setWorkOrderNo(voucherTrackingService.generateUniqueNo(VoucherType.USBA_STR));
            storeWorkOrder.setId(workOrderId);
        }
        return storeWorkOrder;
    }

    /**
     * This method will generate WorkOrder Component
     *
     * @param unserviceableId {@link Long}
     * @return                {@link WorkOrderComponent}
     */
    public WorkOrderComponent getWorkOrderComponent(Long unserviceableId) {
        WorkOrderComponent workOrderComponent;
        workOrderComponent = unserviceablePartService.getWorkOrderComponent(unserviceableId);
        // workOrderComponent.setSerialNo(storePartSerialService.getSerialByPartId(unserviceableId)); //TODO Serial no fix
        return workOrderComponent;
    }

    public String getWorkOrderNoByUnserviceablePartId(Long unserviceablePartId) {
        return storeWorkOrderRepository.findWorkOrderNoByUnserviceablePartId(unserviceablePartId);
    }

    /**
     * Change active status
     *
     * @param id       which user want to change status
     * @param isActive boolean field
     */
    @Override
    public void updateActiveStatus(Long id, Boolean isActive) {
        StoreWorkOrder storeWorkOrder = findByIdUnfiltered(id);

        Long subModuleItemId = helper.getSubModuleItemId();

        workFlowUtil.validateUpdatability(storeWorkOrder.getWorkFlowActionId());

        if (isActive == TRUE) {
            workFlowUtil.validateWorkflow(subModuleItemId, List.of(storeWorkOrder.getWorkFlowActionId(),
                    workFlowActionService.getNavigatedAction(false, storeWorkOrder.getWorkFlowAction()).getId()));
            storeWorkOrder.setWorkFlowAction(workFlowUtil.revertAndFindPrevAction(storeWorkOrder.getWorkFlowAction(),
                    STORE_WORK_ORDER, storeWorkOrder.getId()));
            storeWorkOrder.setIsRejected(false);
        } else {
            workFlowUtil.validateWorkflow(subModuleItemId, Collections.singletonList(storeWorkOrder.getWorkFlowActionId()));
        }
        storeWorkOrder.setIsActive(isActive);
        saveItem(storeWorkOrder);
    }

    @Transactional
    public void makeDecision(Long id, ApprovalRequestDto approvalRequestDto) {
        StoreWorkOrder storeWorkOrder = findByIdUnfiltered(id);
        Long subModuleItemId = helper.getSubModuleItemId();

        workFlowUtil.validateUpdatability(storeWorkOrder.getWorkFlowActionId());
        workFlowUtil.validateWorkflow(subModuleItemId, Collections.singletonList(storeWorkOrder.getWorkFlowActionId()));

        if (approvalRequestDto.getApprove() == Boolean.TRUE) {
            approvalStatusService.create(ApprovalStatusDto.of(storeWorkOrder.getId(), STORE_WORK_ORDER,
                    storeWorkOrder.getWorkFlowAction()));
            storeWorkOrder.setWorkFlowAction(workFlowActionService.getNavigatedAction(true,
                    storeWorkOrder.getWorkFlowAction()));
        } else {
            if (!StringUtils.hasText(approvalRequestDto.getRejectedDesc())) {
                throw EngineeringManagementServerException.notFound
                        (ErrorId.REJECTED_DESCRIPTION_CAN_NOT_BE_EMPTY);
            }
            storeWorkOrder.setIsRejected(true);
            storeWorkOrder.setRejectedDesc(approvalRequestDto.getRejectedDesc());
        }
        super.saveItem(storeWorkOrder);
    }
}
