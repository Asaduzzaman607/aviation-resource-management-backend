package com.digigate.engineeringmanagement.procurementmanagement.service;

import com.digigate.engineeringmanagement.common.constant.ApprovalStatusType;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.constant.VoucherType;
import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.common.entity.User;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.payload.request.search.WorkFlowDto;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.service.UserService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.common.util.Helper;
import com.digigate.engineeringmanagement.common.util.WorkFlowUtil;
import com.digigate.engineeringmanagement.configurationmanagement.dto.projection.WorkFlowActionProjection;
import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.WorkFlowAction;
import com.digigate.engineeringmanagement.configurationmanagement.service.administration.WorkFlowActionService;
import com.digigate.engineeringmanagement.procurementmanagement.constant.InputType;
import com.digigate.engineeringmanagement.procurementmanagement.constant.OrderType;
import com.digigate.engineeringmanagement.procurementmanagement.constant.RfqType;
import com.digigate.engineeringmanagement.procurementmanagement.constant.VendorRequestType;
import com.digigate.engineeringmanagement.procurementmanagement.dto.projection.*;
import com.digigate.engineeringmanagement.procurementmanagement.dto.request.*;
import com.digigate.engineeringmanagement.procurementmanagement.dto.response.*;
import com.digigate.engineeringmanagement.procurementmanagement.entity.*;
import com.digigate.engineeringmanagement.procurementmanagement.repository.PartOrderRepository;
import com.digigate.engineeringmanagement.procurementmanagement.util.CommonUtil;
import com.digigate.engineeringmanagement.procurementmanagement.util.CsUtilService;
import com.digigate.engineeringmanagement.procurementmanagement.util.PoDuplicateQuotation;
import com.digigate.engineeringmanagement.status.service.DemandStatusService;
import com.digigate.engineeringmanagement.status.serviceImpl.DemandStatusServiceImpl;
import com.digigate.engineeringmanagement.storemanagement.constant.RemarkType;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.ApprovalStatus;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.ProcurementRequisition;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.PartRemark;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.UsernameProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.ApprovalRequestDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.ApprovalStatusDto;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand.ApprovalStatusViewModel;
import com.digigate.engineeringmanagement.storemanagement.service.StoreVoucherTrackingService;
import com.digigate.engineeringmanagement.storemanagement.service.storeconfiguration.PartRemarkService;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.ApprovalStatusService;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.ProcurementRequisitionService;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.StoreDemandDetailsService;
import com.digigate.engineeringmanagement.storemanagement.util.SortChanger;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.*;
import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.WORKFLOW_ACTION_ORDER.INITIAL_ORDER;
import static com.digigate.engineeringmanagement.common.constant.ApprovalStatusType.LOGISTIC_PURCHASE_ORDER;
import static com.digigate.engineeringmanagement.common.constant.ApprovalStatusType.PURCHASE_ORDER;
import static java.lang.Boolean.TRUE;

@Service
public class PartOrderService extends AbstractSearchService<PartOrder, PoInternalDto, PoSearchDto> {
    private final PartOrderItemService partOrderItemService;
    private final StoreVoucherTrackingService voucherTrackingService;
    private final ProcurementRequisitionService procurementRequisitionService;
    private final CsDetailService csDetailService;
    private final VendorQuotationService vendorQuotationService;
    private final UserService userService;
    private final WorkFlowActionService workFlowActionService;
    private final WorkFlowUtil workFlowUtil;
    private final Helper helper;
    private final ApprovalStatusService approvalStatusService;
    private final PartOrderRepository partOrderRepository;
    private final ProcurementManualPoService procurementManualPoService;
    private final LogisticManualPoService logisticManualPoService;
    private final RequisitionToManualPoService requisitionToManualPoService;
    private final PartRemarkService partRemarkService;
    private final DemandStatusService demandStatusService;
    private final VendorQuotationInvoiceDetailService vendorQuotationInvoiceDetailService;
    private final StoreDemandDetailsService storeDemandDetailsService;
    private final DemandStatusServiceImpl demandStatusServiceImpl;
    private final PoDuplicateQuotation poDuplicateQuotation;
    private final CsUtilService csUtilService;
    private final CommonUtil commonUtil;

    public PartOrderService(PartOrderRepository partOrderRepository,
                            PartOrderItemService partOrderItemService,
                            StoreVoucherTrackingService voucherTrackingService,
                            ProcurementRequisitionService procurementRequisitionService,
                            CsDetailService csDetailService,
                            VendorQuotationService vendorQuotationService,
                            UserService userService,
                            WorkFlowActionService workFlowActionService,
                            WorkFlowUtil workFlowUtil,
                            Helper helper,
                            ApprovalStatusService approvalStatusService,
                            ProcurementManualPoService procurementManualPoService,
                            LogisticManualPoService logisticManualPoService,
                            RequisitionToManualPoService requisitionToManualPoService,
                            PartRemarkService partRemarkService,
                            DemandStatusService demandStatusService,
                            VendorQuotationInvoiceDetailService vendorQuotationInvoiceDetailService,
                            StoreDemandDetailsService storeDemandDetailsService,
                            DemandStatusServiceImpl demandStatusServiceImpl,
                            PoDuplicateQuotation poDuplicateQuotation,
                            CsUtilService csUtilService, CommonUtil commonUtil) {
        super(partOrderRepository);
        this.partOrderItemService = partOrderItemService;
        this.voucherTrackingService = voucherTrackingService;
        this.procurementRequisitionService = procurementRequisitionService;
        this.csDetailService = csDetailService;
        this.vendorQuotationService = vendorQuotationService;
        this.userService = userService;
        this.workFlowActionService = workFlowActionService;
        this.workFlowUtil = workFlowUtil;
        this.helper = helper;
        this.approvalStatusService = approvalStatusService;
        this.partOrderRepository = partOrderRepository;
        this.procurementManualPoService = procurementManualPoService;
        this.logisticManualPoService = logisticManualPoService;
        this.requisitionToManualPoService = requisitionToManualPoService;
        this.partRemarkService = partRemarkService;
        this.demandStatusService = demandStatusService;
        this.vendorQuotationInvoiceDetailService = vendorQuotationInvoiceDetailService;
        this.storeDemandDetailsService = storeDemandDetailsService;
        this.demandStatusServiceImpl = demandStatusServiceImpl;
        this.poDuplicateQuotation = poDuplicateQuotation;
        this.csUtilService = csUtilService;
        this.commonUtil = commonUtil;
    }

    public List<RfqPartViewModel> getRfqPartViewModelLIst(Long partOrderId) {
        Set<Long> poItemIds = partOrderItemService.findAllByPartOrderId(partOrderId).stream()
                .map(PartOrderItemProjection::getId).collect(Collectors.toSet());
        return partOrderItemService.getRfqPartViewModelsByIdIn(poItemIds);
    }

    public List<RfqPartViewModel> getLogisticRfqPartViewModelList(Long partOrderId) {
        PoResponseDto partOrderList = getSingle(partOrderId);
        VendorQuotationViewModel vendorQuotationViewModel = partOrderList.getVendorQuotationViewModel();
        return vendorQuotationViewModel.getVendorQuotationDetails().stream().map(this::convertViewModel).collect(Collectors.toList());
    }

    private RfqPartViewModel convertViewModel(VendorQuotationInvoiceDetailViewModel vendorQuotationInvoiceDetailViewModel) {
        return RfqPartViewModel.builder()
                .partNo(vendorQuotationInvoiceDetailViewModel.getPartNo())
                .partId(vendorQuotationInvoiceDetailViewModel.getPartId())
                .partDescription(vendorQuotationInvoiceDetailViewModel.getPartDescription())
                .unitMeasurementId(vendorQuotationInvoiceDetailViewModel.getUnitMeasurementId())
                .unitMeasurementCode(vendorQuotationInvoiceDetailViewModel.getUnitMeasurementCode())
                .quantityRequested(vendorQuotationInvoiceDetailViewModel.getPartQuantity())
                .id(vendorQuotationInvoiceDetailViewModel.getItemId())
                .build();
    }

    public List<PartOrderProjection> findByIdIn(Set<Long> collectionOfPartOrderIds) {
        return partOrderRepository.findByCsDetailIdInAndIsActiveTrue(collectionOfPartOrderIds);
    }

    public List<PartOrder> findByPartOrderIdIn(Set<Long> collectionOfPartOrderIds) {
        return partOrderRepository.findPartOrderByIdIn(collectionOfPartOrderIds);
    }

    @Override
    public PartOrder create(PoInternalDto poInternalDto) {
        List<WorkFlowAction> sortedWorkflowActions = workFlowActionService.getSortedWorkflowActions(Sort.Direction.ASC);
        WorkFlowAction workFlowAction = workFlowActionService.getByIndex(INITIAL_ORDER, sortedWorkflowActions);
        Long subModuleItemId = helper.getSubModuleItemId();
        workFlowUtil.validateWorkflow(subModuleItemId, Collections.singletonList(workFlowAction.getId()));

        PartOrder partOrder = selectToPopulatePoForCreate(poInternalDto);
        Long oldPoId = partOrder.getId();

        partOrder.setWorkFlowAction(workFlowActionService.getByIndex(INITIAL_ORDER + INT_ONE, sortedWorkflowActions));
        partOrder.setSubmoduleItemId(subModuleItemId);

        /** SAVE PO */
        PartOrder entity = saveItem(partOrder);

        /** for approval remark we need to avoid duplicate approval status creation */
        if(Objects.isNull(oldPoId)){
            approvalStatusService.create(ApprovalStatusDto.of(entity.getId(), workflow(poInternalDto.getRfqType()), workFlowAction));
        }

        VendorQuotation quotation = quotationSelection(entity, poInternalDto);
        quotation.setPartOrder(entity);
        vendorQuotationService.saveItem(quotation);
        partOrderItemService.saveOrUpdate(poInternalDto, entity);

        return partOrder;
    }

    public List<PartOrder> create(PartOrderListDto partOrderListDto) {

        Set<Long> csDetailIdSet = partOrderListDto.getPartOrderDtoList().stream().map(PartOrderDto::getCsDetailId).collect(Collectors.toSet());

        Map<Long, Long> csdIdAndPoIdMap = partOrderRepository.findByCsDetailIdIn(csDetailIdSet).stream().collect(
                Collectors.toMap(PartOrder::getCsDetailId, PartOrder::getId, (a, b)-> b));

        List<PoInternalDto> poInternalDtoList = partOrderListDto.getPartOrderDtoList().stream().map(partOrderDto -> {
                    PoInternalDto poInternalDto = new PoInternalDto();
                    poInternalDto.setId(csdIdAndPoIdMap.getOrDefault(partOrderDto.getCsDetailId(), null)); //set part order
                    poInternalDto.setTac(partOrderListDto.getTac());
                    poInternalDto.setRemark(partOrderListDto.getRemark());
                    poInternalDto.setCsDetailId(partOrderDto.getCsDetailId());
                    poInternalDto.setItemIdList(partOrderDto.getItemIdList());
                    poInternalDto.setRfqType(partOrderListDto.getRfqType());
                    poInternalDto.setOrderType(partOrderListDto.getOrderType());
                    poInternalDto.setShipTo(partOrderListDto.getShipTo());
                    poInternalDto.setInvoiceTo(partOrderListDto.getInvoiceTo());
                    poInternalDto.setDiscount(partOrderListDto.getDiscount());
                    poInternalDto.setDiscountType(partOrderListDto.getDiscountType());
                    poInternalDto.setOrderNo(partOrderListDto.getOrderNo());
                    poInternalDto.setCompanyName(partOrderListDto.getCompanyName());
                    poInternalDto.setPickUpAddress(partOrderListDto.getPickUpAddress());
                    return poInternalDto;
                })
                .collect(Collectors.toList());
        return poInternalDtoList.stream().map(this::create).collect(Collectors.toList());
    }

    @Transactional
    public PartOrder update(PoInternalDto poInternalDto, Long id, ApprovalStatusType approvalStatusType) {
        PartOrder partOrder = findByIdUnfiltered(id);
        Long subModuleItemId = helper.getSubModuleItemId();

        WorkFlowAction currentAction = partOrder.getWorkFlowAction();
        workFlowUtil.validateWorkflow(subModuleItemId, Arrays.asList(currentAction.getId(),
                workFlowActionService.getNavigatedAction(false, currentAction).getId()));

        /** PO select for update */
        partOrder = selectToPopulatePoForUpdate(poInternalDto, partOrder);

        partOrderItemService.saveOrUpdate(poInternalDto, partOrder);

        if (partOrder.getInputType() == InputType.CS) {
            VendorQuotationDto vendorQuotationDto = poInternalDto.getVendorQuotationDto();
            vendorQuotationService.update(vendorQuotationDto, vendorQuotationDto.getId());
            changeRelatedCSWorkFlow(partOrder);
        }
        WorkFlowAction resetAction = approvalStatusService.resetAction(approvalStatusType, id);
        partRemarkService.deleteByParentIdAndRemarkType(partOrder.getId(), getRemarkType(partOrder.getRfqType()));
        partOrder.setWorkFlowAction(resetAction);
        PartOrder entity = saveItem(partOrder);
        demandStatusUpdate(poInternalDto, entity);
        return entity;
    }
    private void demandStatusUpdate(PoInternalDto poInternalDto, PartOrder partOrder) {

        if (poInternalDto.getInputType().equals(InputType.MANUAL)) {

            List<VendorQuotationInvoiceDetail> vendorQuotationInvoiceDetailList = poInternalDto.getIqItems();
            Set<Long> vendorQuotationInvoiceIds = vendorQuotationInvoiceDetailList.stream().map(AbstractDomainBasedEntity::getId).collect(Collectors.toSet());
            List<IqItemProjection> iqItemProjections = vendorQuotationInvoiceDetailService.findDetailsByIdInForLogistic(vendorQuotationInvoiceIds, VendorRequestType.QUOTATION);

            if (poInternalDto.getRfqType().equals(RfqType.PROCUREMENT)) {
                for (VendorQuotationInvoiceDetail vendorQuotationInvoiceDetail : vendorQuotationInvoiceDetailList) {

                    demandStatusServiceImpl.deleteAllDemandStatusForPO(
                            vendorQuotationInvoiceDetail.getRequisitionItem().getDemandItem().getStoreDemand().getId(),
                            partOrder.getId(),
                            vendorQuotationInvoiceDetail.getId(),
                            getVoucherType(poInternalDto.getOrderType()));

                    demandStatusService.entityUpdateForPO(
                            getPartIdForManualOrProcurement(vendorQuotationInvoiceDetail),
                            vendorQuotationInvoiceDetail.getRequisitionItem().getProcurementRequisition().getId(),
                            vendorQuotationInvoiceDetail.getRequisitionItem().getDemandItem().getStoreDemand().getId(),
                            partOrder.getId(),
                            vendorQuotationInvoiceDetail.getId(),
                            vendorQuotationInvoiceDetail.getPartQuantity(),
                            partOrder.getWorkFlowAction().getId(),
                            getVoucherType(poInternalDto.getOrderType()),
                            RfqType.PROCUREMENT.name(),
                            partOrder.getIsActive(),
                            poInternalDto.getInputType(),
                            partOrder.getIsRejected()
                    );
                }
            } else {
                for (IqItemProjection iqItemProjection : iqItemProjections) {

                    demandStatusServiceImpl.deleteAllDemandStatusForPO(
                            storeDemandDetailsService.findById(iqItemProjection.getDemandItemId()).getStoreDemand().getId(),
                            partOrder.getId(),
                            iqItemProjection.getId(),
                            getVoucherType(poInternalDto.getOrderType()));

                    demandStatusService.entityUpdateForPO(
                            Objects.nonNull(iqItemProjection.getAltPartId())? iqItemProjection.getAltPartId() : iqItemProjection.getPartId(),
                            poInternalDto.getPPoId(),
                            storeDemandDetailsService.findById(iqItemProjection.getDemandItemId()).getStoreDemand().getId(),
                            partOrder.getId(),
                            iqItemProjection.getId(),
                            iqItemProjection.getPartQuantity(),
                            partOrder.getWorkFlowAction().getId(),
                            getVoucherType(poInternalDto.getOrderType()),
                            RfqType.LOGISTIC.name(),
                            partOrder.getIsActive(),
                            poInternalDto.getInputType(),
                            partOrder.getIsRejected()
                    );
                }
            }
        } else {

            if (poInternalDto.getRfqType().equals(RfqType.PROCUREMENT)) {
                List<VendorQuotationInvoiceDetail> vendorQuotationInvoiceDetailList = partOrder.getPartOrderItemList().stream().map(PartOrderItem::getIqItem).collect(Collectors.toList());

                for (VendorQuotationInvoiceDetail vendorQuotationInvoiceDetail : vendorQuotationInvoiceDetailList) {

                    demandStatusServiceImpl.deleteAllDemandStatusForPO(
                            getPartIdForManualOrProcurement(vendorQuotationInvoiceDetail),
                            partOrder.getId(),
                            vendorQuotationInvoiceDetail.getId(),
                            getVoucherType(poInternalDto.getOrderType()));

                    demandStatusService.entityUpdateForPO(
                            getPartIdForProcurementPO(vendorQuotationInvoiceDetail),
                            partOrder.getCsDetail().getComparativeStatement().getId(),
                            getPartIdForCSOrProcurement(vendorQuotationInvoiceDetail),
                            partOrder.getId(),
                            vendorQuotationInvoiceDetail.getId(),
                            vendorQuotationInvoiceDetail.getPartQuantity(),
                            partOrder.getWorkFlowAction().getId(),
                            getVoucherType(poInternalDto.getOrderType()),
                            RfqType.PROCUREMENT.name(),
                            partOrder.getIsActive(),
                            poInternalDto.getInputType(),
                            partOrder.getIsRejected()
                    );
                }
            } else {
                List<PartOrderItem> partOrderItemList = partOrderItemService.findByPartOrderId(partOrder.getId());

                for (PartOrderItem partOrderItem : partOrderItemList) {

                    demandStatusServiceImpl.deleteAllDemandStatusForPO(
                            partOrderItem.getIqItem().getPoItem().getIqItem().getRequisitionItem().getDemandItem().getStoreDemand().getId(),
                            partOrder.getId(),
                            partOrderItem.getIqItem().getId(),
                            getVoucherType(poInternalDto.getOrderType()));

                    demandStatusService.entityUpdateForPO(
                            getPartIdForCSPartOrder(partOrderItem),
                            partOrder.getCsDetail().getComparativeStatement().getId(),
                            partOrderItem.getIqItem().getPoItem().getIqItem().getRequisitionItem().getDemandItem().getStoreDemand().getId(),
                            partOrder.getId(),
                            partOrderItem.getIqItem().getId(),
                            partOrderItem.getIqItem().getPartQuantity(),
                            partOrder.getWorkFlowAction().getId(),
                            getVoucherType(poInternalDto.getOrderType()),
                            RfqType.LOGISTIC.name(),
                            partOrder.getIsActive(),
                            poInternalDto.getInputType(),
                            partOrder.getIsRejected()
                    );
                }
            }
        }
    }

    @Transactional
    public void makeDecision(Long id, ApprovalRequestDto approvalRequestDto, RfqType rfqType) {
        PartOrder partOrder = findById(id);
        Long subModuleItemId = helper.getSubModuleItemId();

        workFlowUtil.validateUpdatability(partOrder.getWorkFlowActionId());
        workFlowUtil.validateWorkflow(subModuleItemId, Collections.singletonList(partOrder.getWorkFlowActionId()));

        if (approvalRequestDto.getApprove() == TRUE) {
            approvalStatusService.create(ApprovalStatusDto.of(partOrder.getId(), workflow(rfqType), partOrder.getWorkFlowAction()));
            if (StringUtils.isEmpty(approvalRequestDto.getApprovalDesc())) {
                throw EngineeringManagementServerException.notFound(ErrorId.APPROVAL_REMARK_CAN_NOT_BE_EMPTY);
            }
            partRemarkService.saveApproveRemark(partOrder.getId(), partOrder.getWorkFlowAction().getId(),
                    getRemarkType(partOrder.getRfqType()), approvalRequestDto.getApprovalDesc());
            partOrder.setWorkFlowAction(workFlowActionService.getNavigatedAction(true, partOrder.getWorkFlowAction()));
        } else {
            partOrder.setIsRejected(true);
            if (!StringUtils.hasText(approvalRequestDto.getRejectedDesc())) {
                throw EngineeringManagementServerException.notFound
                        (ErrorId.REJECTED_DESCRIPTION_CAN_NOT_BE_EMPTY);
            }
            partOrder.setRejectedDesc(approvalRequestDto.getRejectedDesc());
        }
        PartOrder entity = super.saveItem(partOrder);
        if (rfqType.equals(RfqType.PROCUREMENT)) {
            List<VendorQuotationInvoiceDetail> vendorQuotationInvoiceDetailList = partOrder.getPartOrderItemList().stream().map(PartOrderItem::getIqItem).collect(Collectors.toList());
            for (VendorQuotationInvoiceDetail vendorQuotationInvoiceDetail : vendorQuotationInvoiceDetailList) {
                demandStatusService.updateWithPO(
                        getPartIdForManualOrProcurement(vendorQuotationInvoiceDetail),
                        id,
                        entity.getWorkFlowAction().getId(),
                        vendorQuotationInvoiceDetail.getId(),
                        entity.getIsRejected(),
                        getVoucherType(partOrder.getOrderType()),
                        rfqType.name());
            }
        } else {
            List<VendorQuotationInvoiceDetail> vendorQuotationInvoiceDetailList = partOrder.getPartOrderItemList().stream().map(PartOrderItem::getIqItem).collect(Collectors.toList());
            for (VendorQuotationInvoiceDetail vendorQuotationInvoiceDetail : vendorQuotationInvoiceDetailList) {
                demandStatusService.updateWithPO(
                        getPartIdForProcurement(vendorQuotationInvoiceDetail),
                        id,
                        entity.getWorkFlowAction().getId(),
                        vendorQuotationInvoiceDetail.getId(),
                        entity.getIsRejected(),
                        getVoucherType(partOrder.getOrderType()),
                        rfqType.name());
            }
        }

    }

    public void updateActiveStatus(Long id, Boolean isActive, RfqType rfqType) {
        PartOrder partOrder = findByIdUnfiltered(id);
        workFlowUtil.validateUpdatability(partOrder.getWorkFlowActionId());
        partOrder.setIsActive(isActive);

        if (isActive == TRUE) {
            WorkFlowAction workFlowAction = workFlowActionService.getNavigatedAction(false, partOrder.getWorkFlowAction());
            workFlowUtil.validateWorkflow(helper.getSubModuleItemId(), List.of(partOrder.getWorkFlowActionId(), workFlowAction.getId()));
            partRemarkService.revertPreviousActionRemarks(partOrder.getId(), workFlowAction, getRemarkType(partOrder.getRfqType()));
            partOrder.setWorkFlowAction(workFlowUtil.revertAndFindPrevAction(partOrder.getWorkFlowAction(), workflow(rfqType),
                    partOrder.getId()));
            partOrder.setIsRejected(false);
            setDemandStatusIsActiveUpdatedValue(partOrder);
            setDemandStatusRejectedUpdatedValue(partOrder);
        } else {
            workFlowUtil.validateWorkflow(helper.getSubModuleItemId(), Collections.singletonList(partOrder.getWorkFlowActionId()));
            setDemandStatusIsActiveUpdatedValue(partOrder);
        }

        super.saveItem(partOrder);
    }

    @Override
    public PageData search(PoSearchDto dto, Pageable pageable) {
        pageable = SortChanger.descendingSortByCreatedAt(pageable);
        Page<PartOrder> pagedData = null;
        List<WorkFlowActionProjection> approvedActionsForUser = new ArrayList<>();
        switch (dto.getType()) {
            case PENDING:
                Set<Long> pendingSearchWorkFlowIds = workFlowUtil.findPendingWorkFlowIds(approvedActionsForUser);
                if (CollectionUtils.isEmpty(pendingSearchWorkFlowIds)) {
                    pagedData = Page.empty();
                    break;
                }
                pagedData = customBuildSpecification(dto, pageable, pendingSearchWorkFlowIds, false);
                break;
            case APPROVED:
                Long approvedId = workFlowActionService.findFinalAction().getId();
                pagedData = customBuildSpecification(dto, pageable, Collections.singleton(approvedId), null);
                break;
            case REJECTED:
                pagedData = customBuildSpecification(dto, pageable, new HashSet<>(), true);
                break;
            case ALL:
                pagedData = customBuildSpecification(dto, pageable, new HashSet<>(), null);
                break;
        }

        return PageData.builder()
                .model(getAllResponse(pagedData.getContent(), approvedActionsForUser, dto.getRfqType()))
                .totalPages(pagedData.getTotalPages())
                .totalElements(pagedData.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    private Page<PartOrder> customBuildSpecification(PoSearchDto searchDto,
                                                     Pageable pageable,
                                                     Set<Long> actionIds,
                                                     Boolean isRejected) {

        CustomSpecification<PartOrder> customSpecification = new CustomSpecification<>();
        Specification<PartOrder> specification = Specification.where(
                customSpecification.equalSpecificationAtRoot(searchDto.getIsActive(), IS_ACTIVE_FIELD)
                        .and(customSpecification.equalSpecificationAtRoot(isRejected, IS_REJECTED_FIELD))
                        .and(customSpecification.inSpecificationAtRoot(actionIds, WORKFLOW_ACTION_ID))
                        .and(customSpecification.equalSpecificationAtRoot(searchDto.getRfqType(), RFQ_TYPE))
                        .and(customSpecification.likeSpecificationAtRoot(searchDto.getQuery(), ORDER_NO))
                        .and(customSpecification.equalSpecificationAtRoot(searchDto.getOrderType(), ORDER_TYPE)));

        return partOrderRepository.findAll(specification, pageable);
    }

    @Override
    protected Specification<PartOrder> buildSpecification(PoSearchDto searchDto) {
        return null;
    }

    @Override
    protected PoResponseDto convertToResponseDto(PartOrder partOrder) {

        List<WorkFlowActionProjection> approvedActionsForUser = new ArrayList<>();
        return getAllResponse(Collections.singletonList(partOrder), approvedActionsForUser,
                partOrder.getRfqType()).stream().findFirst().get();
    }

    @Override
    protected PartOrder convertToEntity(PoInternalDto poInternalDto) {
        PartOrder partOrder = new PartOrder();

        /** Existing order replace with new parts */
        if (Objects.nonNull(poInternalDto.getId())) {
            PartOrder oldPartOrder = findByIdUnfiltered(poInternalDto.getId());
            if(!Objects.equals(oldPartOrder.getWorkFlowActionId(),
                    workFlowActionService.findFinalAction().getId())){
                partOrder = oldPartOrder;
            }
        }
        return populateToEntity(poInternalDto, partOrder);
    }

    @Override
    protected PartOrder updateEntity(PoInternalDto dto, PartOrder entity) {
        if (Objects.nonNull(entity.getInputType()) && !dto.getInputType().equals(entity.getInputType())) {
            throw EngineeringManagementServerException.badRequest(ErrorId.INPUT_TYPE_MUST_NOT_BE_UPDATE);
        }
        return populateToEntity(dto, entity);
    }

    public List<PoResponseDto> getAllResponse(List<PartOrder> partOrderList,
                                              List<WorkFlowActionProjection> approvedActionsForUser,
                                              RfqType rfqType) {

        Set<Long> partOrderIds = partOrderList.stream().map(PartOrder::getId).collect(Collectors.toSet());

        Map<Long, VendorQuotation> vendorQuotationMap = vendorQuotationService.finByPartOrderIdIn(partOrderIds).stream().collect(
                Collectors.toMap(VendorQuotation::getPartOrderId, Function.identity()));

        Map<Long, List<PoItemResponseDto>> poItemListMap = partOrderItemService.getAllResponse(partOrderList, rfqType)
                .stream().collect(Collectors.groupingBy(PoItemResponseDto::getPartOrderId));

        Set<Long> submittedByIdList = partOrderList.stream().map(PartOrder::getSubmittedById).collect(Collectors.toSet());

        Map<Long, UsernameProjection> usernameProjectionMap = userService.findUsernameByIdList(submittedByIdList).stream()
                .collect(Collectors.toMap(UsernameProjection::getId, Function.identity()));

        WorkFlowDto workFlowDto = workFlowUtil.prepareResponseData(partOrderIds, approvedActionsForUser, workflow(rfqType));

        Map<Long, List<PartRemark>> partRemarkList = partRemarkService.findByParentIdAndRemarkType(partOrderIds,
                        getRemarkType(rfqType)).stream().collect(Collectors.groupingBy(PartRemark::getParentId)); //approval  remarks

        Set<Long> csDetailIds = partOrderList.stream().map(PartOrder::getCsDetailId).collect(Collectors.toSet());

        Map<Long, CsDetailProjection> csDetailProjectionMap = csDetailService.findCsDetailByIdIn(csDetailIds).stream()
                .collect(Collectors.toMap(CsDetailProjection::getId, Function.identity()));

        Map<Long, RequisitionPoProjection> requisitionProjectionMap = partOrderRepository.findRequisitionByPartOrderIdIn(partOrderIds)
                .stream().collect(Collectors.toMap(RequisitionPoProjection::getPoId, Function.identity()));

        return partOrderList.stream().map(partOrder -> convertToResponseDto(partOrder,
                        poItemListMap.getOrDefault(partOrder.getId(), new ArrayList<>()),
                        usernameProjectionMap.getOrDefault(partOrder.getSubmittedById(), null),
                        vendorQuotationMap.get(partOrder.getId()),
                        workFlowDto,
                        csDetailProjectionMap.getOrDefault(partOrder.getCsDetailId(), null),
                        requisitionProjectionMap.get(partOrder.getId()),
                        partRemarkList.get(partOrder.getId())))
                .collect(Collectors.toList());
    }

    private PartOrder populateToEntity(PoInternalDto dto, PartOrder entity) {
        if (Objects.isNull(entity.getVoucherNo())) {
            if (Objects.nonNull(dto.getCsDetailId())) {
                CsDetail csDetail = csDetailService.findById(dto.getCsDetailId());
                if (dto.getRfqType().equals(RfqType.PROCUREMENT)) {
                    entity.setVoucherNo(voucherTrackingService.generateUniqueVoucherNo(csDetail.getComparativeStatementId(),
                            getPattern(), csDetail.getComparativeStatement().getComparativeStatementNo()));
                } else {
                    entity.setVoucherNo(voucherTrackingService.generateUniqueVoucherNo(csDetail.getComparativeStatementId(),
                            getPattern(), csDetail.getComparativeStatement().getComparativeStatementNo()));
                }
            } else {
                if (dto.getRfqType().equals(RfqType.PROCUREMENT)) {
                    if(Objects.nonNull(dto.getRequisitionId())){
                        ProcurementRequisition procurementRequisition = procurementRequisitionService.findById(dto.getRequisitionId());
                        entity.setVoucherNo(voucherTrackingService.generateUniqueVoucherNo(procurementRequisition.getId(),
                                getPattern(), procurementRequisition.getVoucherNo()));
                    }else {
                        entity.setVoucherNo(dto.getOrderNo());
                    }
                } else {
                    PartOrder partOrder = findByIdUnfiltered(dto.getPPoId());
                    entity.setVoucherNo(voucherTrackingService.generateUniqueVoucherNo(partOrder.getId(),
                            getPattern(), partOrder.getVoucherNo()));
                }
            }
        }
        if (Objects.nonNull(dto.getCsDetailId())) {
            entity.setCsDetail(csDetailService.findById(dto.getCsDetailId()));
        }
        entity.setSubmittedBy(User.withId(Helper.getAuthUserId()));
        entity.setTac(dto.getTac());
        entity.setOrderNo(dto.getOrderNo());
        entity.setRemark(dto.getRemark());
        entity.setRfqType(dto.getRfqType());
        entity.setInputType(dto.getInputType());
        entity.setDiscountType(dto.getDiscountType());
        entity.setDiscount(dto.getDiscount());
        entity.setOrderType(dto.getOrderType());
        entity.setShipTo(dto.getShipTo());
        entity.setInvoiceTo(dto.getInvoiceTo());
        entity.setCompanyName(dto.getCompanyName());
        entity.setPickUpAddress(dto.getPickUpAddress());
        return entity;
    }

    private VoucherType getPattern() {
        return VoucherType.USBA_C;
    }

    private VoucherType getVoucherType(OrderType orderType) {
        VoucherType voucherType;
        switch (orderType)
        {
            case REPAIR:
                voucherType = VoucherType.RO;
                break;
            case LOAN:
                voucherType = VoucherType.LO;
                break;
            case EXCHANGE:
                voucherType = VoucherType.PO;
                break;
            case PURCHASE:
                voucherType = VoucherType.ORDER;
                break;
            default:
                voucherType = VoucherType.ORDER;
                break;
        }
        return voucherType;
    }

    private PoResponseDto convertToResponseDto(PartOrder partOrder,
                                               List<PoItemResponseDto> poItemResponseDtoList,
                                               UsernameProjection usernameProjection,
                                               VendorQuotation vendorQuotation,
                                               WorkFlowDto workFlowDto,
                                               CsDetailProjection csDetailProjection,
                                               RequisitionPoProjection requisitionPoProjection,
                                               List<PartRemark> partRemarks) {

        List<ApprovalStatus> approvalStatuses = workFlowDto.getStatusMap().getOrDefault(partOrder.getId(), new ArrayList<>());
        Map<Long, ApprovalStatus> workFlowActionMap = approvalStatuses.stream().collect(Collectors.toMap(ApprovalStatus::getWorkFlowActionId, Function.identity(), (a, b) -> b));
        WorkFlowAction workFlowAction = workFlowDto.getWorkFlowActionMap().get(partOrder.getWorkFlowActionId());

        PoResponseDto poResponseDto = new PoResponseDto();
        poResponseDto.setId(partOrder.getId());
        poResponseDto.setVoucherNo(partOrder.getVoucherNo());
        poResponseDto.setOrderNo(partOrder.getOrderNo());
        if (Objects.nonNull(csDetailProjection)) {
            poResponseDto.setCsDetailId(csDetailProjection.getId());
            poResponseDto.setCsId(csDetailProjection.getComparativeStatementId());
            poResponseDto.setCsNo(csDetailProjection.getComparativeStatementComparativeStatementNo());
        }
        if (Objects.nonNull(usernameProjection)) {
            poResponseDto.setSubmittedById(usernameProjection.getId());
            poResponseDto.setSubmittedByName(usernameProjection.getLogin());
            poResponseDto.setEmployeeId(usernameProjection.getEmployeeId());
            poResponseDto.setEmployeeName(usernameProjection.getEmployeeName());
            poResponseDto.setDesignationId(usernameProjection.getEmployeeDesignationId());
            poResponseDto.setDesignationName(usernameProjection.getEmployeeDesignationName());
        }
        poResponseDto.setTac(partOrder.getTac());
        poResponseDto.setDiscountType(partOrder.getDiscountType());
        poResponseDto.setDiscount(partOrder.getDiscount());
        poResponseDto.setRemark(partOrder.getRemark());
        poResponseDto.setInputType(partOrder.getInputType());
        poResponseDto.setOrderType(partOrder.getOrderType());
        poResponseDto.setOrderNo(partOrder.getOrderNo());
        poResponseDto.setRfqType(partOrder.getRfqType());
        poResponseDto.setShipTo(partOrder.getShipTo());
        poResponseDto.setInvoiceTo(partOrder.getInvoiceTo());
        poResponseDto.setRejectedDesc(partOrder.getRejectedDesc());
        poResponseDto.setIsRejected(partOrder.getIsRejected());
        poResponseDto.setCompanyName(partOrder.getCompanyName());
        poResponseDto.setPickUpAddress(partOrder.getPickUpAddress());
        if (Objects.nonNull(requisitionPoProjection)) {
            poResponseDto.setRequisitionId(commonUtil.isInvisible(requisitionPoProjection.getRequisitionNo()) ?
                    null : requisitionPoProjection.getId());
            poResponseDto.setRequisitionNo(requisitionPoProjection.getRequisitionNo());
        }
        Map<Long, VendorQuotationInvoiceDetailViewModel> vendorQuotationInvoiceDetailViewModelMap = new HashMap<>();

        if (Objects.nonNull(vendorQuotation)) {
            poResponseDto.setVendorResponse(vendorQuotationService.convertToVendorResponseDto(vendorQuotation).toString());
            Set<Long> iqItems = poItemResponseDtoList.stream().map(PoItemResponseDto::getItemId).collect(Collectors.toSet());

            VendorQuotationViewModel quotationViewModel = vendorQuotationService.convertToResponseDto(vendorQuotation);
            vendorQuotationInvoiceDetailViewModelMap = quotationViewModel.
                    getVendorQuotationDetails().stream().collect(Collectors.toMap(VendorQuotationInvoiceDetailViewModel::getId, Function.identity()));

            quotationViewModel.setVendorQuotationDetails(quotationViewModel.getVendorQuotationDetails().stream()
                    .filter(detail -> iqItems.contains(detail.getId())).collect(Collectors.toList()));

            poResponseDto.setVendorQuotationViewModel(quotationViewModel);
        }

        for (PoItemResponseDto poItemResponseDto : poItemResponseDtoList) {
            VendorQuotationInvoiceDetailViewModel viewModel = vendorQuotationInvoiceDetailViewModelMap.getOrDefault(
                    poItemResponseDto.getItemId(), new VendorQuotationInvoiceDetailViewModel());
            poItemResponseDto.setVendorQuotationInvoiceDetails(viewModel);
        }
        poResponseDto.setPoItemResponseDtoList(poItemResponseDtoList);

        poResponseDto.setWorkFlowActionId(partOrder.getWorkFlowActionId());
        if (Objects.nonNull(workFlowAction)) {
            poResponseDto.setWorkflowName(workFlowAction.getName());
            poResponseDto.setWorkflowOrder(workFlowAction.getOrderNumber());
        }
        poResponseDto.setActionEnabled(workFlowDto.getActionableIds().contains(partOrder.getWorkFlowActionId()));
        poResponseDto.setEditable(workFlowDto.getEditableIds().contains(partOrder.getWorkFlowActionId()));
        poResponseDto.setApprovalStatuses(approvalStatuses.stream().map(approvalStatus ->
                        ApprovalStatusViewModel.from(approvalStatus, workFlowDto.getNamesFromApprovalStatuses()))
                .collect(Collectors.toMap(ApprovalStatusViewModel::getWorkFlowActionId,
                        Function.identity(), (a, b) -> b)));

        if (CollectionUtils.isNotEmpty(partRemarks)) {
            poResponseDto.setApprovalRemarksResponseDtoList(partRemarks.stream().map(partRemark ->
                    partRemarkService.prepareApprovalRemarkResponse(partRemark, workFlowActionMap, workFlowDto.getNamesFromApprovalStatuses())).collect(Collectors.toList()));
        }

        return poResponseDto;
    }

    private ApprovalStatusType workflow(RfqType rfqType) {
        return rfqType == RfqType.PROCUREMENT ? PURCHASE_ORDER : LOGISTIC_PURCHASE_ORDER;
    }

    private PartOrder selectToPopulatePoForCreate(PoInternalDto poInternalDto) {

        if (poInternalDto.getInputType() == InputType.CS) {
            return convertToEntity(poInternalDto);
        } else if (poInternalDto.getRfqType() == RfqType.PROCUREMENT) {
            if (Objects.nonNull(poInternalDto.getRequisitionId())) {
                return convertToEntity(requisitionToManualPoService.populateToManualEntity(poInternalDto));
            } else {
                return convertToEntity(procurementManualPoService.populateToManualEntity(poInternalDto));
            }
        } else {
            return convertToEntity(logisticManualPoService.populateToManualEntity(poInternalDto));
        }
    }

    private PartOrder selectToPopulatePoForUpdate(PoInternalDto poInternalDto, PartOrder partOrder) {

        if (poInternalDto.getInputType() == InputType.CS) {
            return updateEntity(poInternalDto, partOrder);
        } else if (poInternalDto.getRfqType() == RfqType.PROCUREMENT) {
            if (Objects.nonNull(poInternalDto.getRequisitionId())) {
                return updateEntity(
                        requisitionToManualPoService.populateToManualEntity(poInternalDto, partOrder),
                        partOrder);
            } else {
                return updateEntity(
                        procurementManualPoService.populateToManualEntity(poInternalDto, partOrder),
                        partOrder);
            }
        } else {
            return updateEntity(
                    logisticManualPoService.populateToManualEntity(poInternalDto, partOrder),
                    partOrder);
        }
    }

    private void setDemandStatusIsActiveUpdatedValue(PartOrder partOrder) {

        if (partOrder.getRfqType().equals(RfqType.PROCUREMENT)) {
            if (partOrder.getInputType().equals(InputType.MANUAL)) {
                List<PartOrderItem> partOrderItemList = partOrderItemService.findByPartOrderId(partOrder.getId());
                partOrderItemList.forEach(partOrderItem -> {
                    demandStatusService.updateActiveStatusForPO(
                            partOrderItem.getIqItem().getRequisitionItem().getDemandItem().getStoreDemand().getId(),
                            partOrder.getId(),
                            getPartIdForManualProcurement(partOrderItem),
                            getVoucherType(partOrder.getOrderType()),
                            partOrderItem.getIqItem().getId(),
                            partOrder.getIsActive(),
                            partOrder.getWorkFlowAction().getId(),
                            RfqType.PROCUREMENT.name());
                });
            } else {
                List<VendorQuotationInvoiceDetail> vendorQuotationInvoiceDetailList =
                        partOrder.getPartOrderItemList().stream().map(PartOrderItem::getIqItem).collect(Collectors.toList());

                vendorQuotationInvoiceDetailList.forEach(vendorQuotationInvoiceDetail ->
                        demandStatusService.updateActiveStatusForPO(
                                vendorQuotationInvoiceDetail.getRequisitionItem().getDemandItem().getStoreDemand().getId(),
                                partOrder.getId(),
                                getPartIdForManualOrProcurementOrCs(vendorQuotationInvoiceDetail),
                                getVoucherType(partOrder.getOrderType()),
                                vendorQuotationInvoiceDetail.getId(),
                                partOrder.getIsActive(),
                                partOrder.getWorkFlowAction().getId(),
                                RfqType.PROCUREMENT.name()));

            }
        } else {
            if (partOrder.getInputType().equals(InputType.MANUAL)) {
                List<PartOrderItem> partOrderItemList = partOrderItemService.findByPartOrderId(partOrder.getId());
                partOrderItemList.forEach(partOrderItem -> {
                    demandStatusService.updateActiveStatusForPO(
                            partOrderItem.getIqItem().getPoItem().getIqItem().getRequisitionItem().getDemandItem().getStoreDemand().getId(),
                            partOrder.getId(),
                            getPartIdForManualOrCsPartOrder(partOrderItem),
                            getVoucherType(partOrder.getOrderType()),
                            partOrderItem.getIqItem().getId(),
                            partOrder.getIsActive(),
                            partOrder.getWorkFlowAction().getId(),
                            RfqType.LOGISTIC.name());
                });
            } else {

                List<PartOrderItem> partOrderItemList = partOrderItemService.findByPartOrderId(partOrder.getId());
                partOrderItemList.forEach(partOrderItem -> {
                    demandStatusService.updateActiveStatusForPO(
                            partOrderItem.getIqItem().getPoItem().getIqItem().getRequisitionItem().getDemandItem().getStoreDemand().getId(),
                            partOrder.getId(),
                            getPartIdForManualOrCsPartOrder(partOrderItem),
                            getVoucherType(partOrder.getOrderType()),
                            partOrderItem.getIqItem().getId(),
                            partOrder.getIsActive(),
                            partOrder.getWorkFlowAction().getId(),
                            RfqType.LOGISTIC.name());
                });

            }
        }

    }

    private void setDemandStatusRejectedUpdatedValue(PartOrder partOrder) {

        if (partOrder.getRfqType().equals(RfqType.PROCUREMENT)) {
            if (partOrder.getInputType().equals(InputType.MANUAL)) {
                List<PartOrderItem> partOrderItemList = partOrderItemService.findByPartOrderId(partOrder.getId());
                partOrderItemList.forEach(partOrderItem -> {
                    demandStatusService.updateRejectedStatusForPO(
                            partOrderItem.getIqItem().getRequisitionItem().getDemandItem().getStoreDemand().getId(),
                            partOrder.getId(),
                            getPartIdForManualProcurement(partOrderItem),
                            getVoucherType(partOrder.getOrderType()),
                            partOrderItem.getIqItem().getId(),
                            partOrder.getIsRejected(),
                            partOrder.getWorkFlowAction().getId(),
                            RfqType.PROCUREMENT.name());
                });
            } else {
                List<VendorQuotationInvoiceDetail> vendorQuotationInvoiceDetailList =
                        partOrder.getPartOrderItemList().stream().map(PartOrderItem::getIqItem).collect(Collectors.toList());

                vendorQuotationInvoiceDetailList.forEach(vendorQuotationInvoiceDetail ->
                        demandStatusService.updateRejectedStatusForPO(
                                vendorQuotationInvoiceDetail.getRequisitionItem().getDemandItem().getStoreDemand().getId(),
                                partOrder.getId(),
                                getPartIdForManualOrProcurementOrCs(vendorQuotationInvoiceDetail),
                                getVoucherType(partOrder.getOrderType()),
                                vendorQuotationInvoiceDetail.getId(),
                                partOrder.getIsRejected(),
                                partOrder.getWorkFlowAction().getId(),
                                RfqType.PROCUREMENT.name()));

            }
        } else {
            if (partOrder.getInputType().equals(InputType.MANUAL)) {
                List<PartOrderItem> partOrderItemList = partOrderItemService.findByPartOrderId(partOrder.getId());
                partOrderItemList.forEach(partOrderItem -> {
                    demandStatusService.updateRejectedStatusForPO(
                            partOrderItem.getIqItem().getPoItem().getIqItem().getRequisitionItem().getDemandItem().getStoreDemand().getId(),
                            partOrder.getId(),
                            getPartIdForManualOrCsPartOrder(partOrderItem),
                            getVoucherType(partOrder.getOrderType()),
                            partOrderItem.getIqItem().getId(),
                            partOrder.getIsRejected(),
                            partOrder.getWorkFlowAction().getId(),
                            RfqType.LOGISTIC.name());
                });
            } else {
                List<PartOrderItem> partOrderItemList = partOrderItemService.findByPartOrderId(partOrder.getId());
                partOrderItemList.forEach(partOrderItem -> {
                    demandStatusService.updateRejectedStatusForPO(
                            partOrderItem.getIqItem().getPoItem().getIqItem().getRequisitionItem().getDemandItem().getStoreDemand().getId(),
                            partOrder.getId(),
                            getPartIdForManualOrCsPartOrder(partOrderItem),
                            getVoucherType(partOrder.getOrderType()),
                            partOrderItem.getIqItem().getId(),
                            partOrder.getIsRejected(),
                            partOrder.getWorkFlowAction().getId(),
                            RfqType.LOGISTIC.name());
                });
            }
        }
    }

    private RemarkType getRemarkType(RfqType rfqType) {
        return rfqType == RfqType.PROCUREMENT ? RemarkType.PROCUREMENT_PO_APPROVAL_REMARK : RemarkType.LOGISTIC_PO_APPROVAL_REMARK;
    }

    private VendorQuotation quotationSelection(PartOrder partOrder, PoInternalDto poInternalDto) {
        if(Objects.nonNull(partOrder.getCsDetail())){
            VendorQuotation oldVendorQuotation = partOrder.getCsDetail().getVendorQuotation();
            if(partOrder.getRfqType().equals(RfqType.PROCUREMENT)){
                if(!partOrder.getId().equals(poInternalDto.getId())){
                    return poDuplicateQuotation.duplicateVendorQuotation(oldVendorQuotation, poInternalDto);
                }
                return poDuplicateQuotation.updateDuplicateVendorQuotation(oldVendorQuotation, partOrder.getId(), poInternalDto);
            }
            return oldVendorQuotation;
        }
        return poInternalDto.getVendorQuotation();
    }

    private void changeRelatedCSWorkFlow(PartOrder partOrder) {
        ComparativeStatement comparativeStatement = partOrder.getCsDetail().getComparativeStatement();
        csUtilService.updatePoRelatedCSWorkFLowToInitialStage(comparativeStatement);
    }
    private Long getPartIdForManualOrProcurement(VendorQuotationInvoiceDetail vendorQuotationInvoiceDetail) {
        Long partId = vendorQuotationInvoiceDetail.getRequisitionItem().getDemandItem().getPart().getId();
        if (Objects.nonNull(vendorQuotationInvoiceDetail.getAlternatePart())) {
            partId = vendorQuotationInvoiceDetail.getAlternatePart().getId();
        }
        return partId;
    }

    private Long getPartIdForProcurement(VendorQuotationInvoiceDetail vendorQuotationInvoiceDetail) {
        Long partId = vendorQuotationInvoiceDetail.getPoItem().getIqItem().getRequisitionItem().getDemandItem().getPart().getId();
        if (Objects.nonNull(vendorQuotationInvoiceDetail.getPoItem().getIqItem().getAlternatePart())) {
            partId = vendorQuotationInvoiceDetail.getPoItem().getIqItem().getAlternatePart().getId();
        }
        return partId;
    }
    private Long getPartIdForManualProcurement(PartOrderItem partOrderItem) {
        Long partId = partOrderItem.getIqItem().getRequisitionItem().getDemandItem().getPart().getId();
        if (Objects.nonNull(partOrderItem.getIqItem().getAlternatePart())) {
            partId = partOrderItem.getIqItem().getAlternatePart().getId();
        }
        return partId;
    }

    private Long getPartIdForProcurementPO(VendorQuotationInvoiceDetail vendorQuotationInvoiceDetail) {
        Long partId = OVERLOAD;
        if (Objects.nonNull(vendorQuotationInvoiceDetail.getAlternatePart())) {
            partId = vendorQuotationInvoiceDetail.getAlternatePart().getId();
        } else {
            if (Objects.nonNull(vendorQuotationInvoiceDetail.getRequisitionItem())) {
                partId = vendorQuotationInvoiceDetail.getRequisitionItem().getDemandItem().getPart().getId();
            }
        }
        return partId;
    }

    private Long getPartIdForCSOrProcurement(VendorQuotationInvoiceDetail vendorQuotationInvoiceDetail) {
        Long partId = OVERLOAD;
        if (Objects.nonNull(vendorQuotationInvoiceDetail.getRequisitionItem())) {
            partId = vendorQuotationInvoiceDetail.getRequisitionItem().getDemandItem().getStoreDemand().getId();
        }
        return partId;
    }

    private Long getPartIdForCSPartOrder(PartOrderItem partOrderItem) {
        Long partId = partOrderItem.getIqItem().getPoItem().getIqItem().getRequisitionItem().getDemandItem().getPart().getId();
        if (Objects.nonNull(partOrderItem.getIqItem().getPoItem().getIqItem().getAlternatePart())) {
            partId = partOrderItem.getIqItem().getPoItem().getIqItem().getAlternatePart().getId();
        }
        return partId;
    }
    private Long getPartIdForManualOrProcurementOrCs(VendorQuotationInvoiceDetail vendorQuotationInvoiceDetail) {
        Long partId = vendorQuotationInvoiceDetail.getRequisitionItem().getDemandItem().getPart().getId();
        if (Objects.nonNull(vendorQuotationInvoiceDetail.getAlternatePart())) {
            partId = vendorQuotationInvoiceDetail.getAlternatePart().getId();
        }
        return partId;
    }
    private Long getPartIdForManualOrCsPartOrder(PartOrderItem partOrderItem) {
        Long partId = partOrderItem.getIqItem().getPoItem().getIqItem().getRequisitionItem().getDemandItem().getPart().getId();
        if (Objects.nonNull(partOrderItem.getIqItem().getPoItem().getIqItem().getAlternatePart())) {
            partId = partOrderItem.getIqItem().getPoItem().getIqItem().getAlternatePart().getId();
        }
        return partId;
    }
}

