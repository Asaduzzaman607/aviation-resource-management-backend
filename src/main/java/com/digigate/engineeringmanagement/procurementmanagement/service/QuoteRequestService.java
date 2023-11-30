package com.digigate.engineeringmanagement.procurementmanagement.service;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ApprovalStatusType;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.constant.VoucherType;
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
import com.digigate.engineeringmanagement.procurementmanagement.constant.RfqType;
import com.digigate.engineeringmanagement.procurementmanagement.dto.projection.QuoteRequestProjection;
import com.digigate.engineeringmanagement.procurementmanagement.dto.request.QuoteRequestDto;
import com.digigate.engineeringmanagement.procurementmanagement.dto.request.RfqRequestDto;
import com.digigate.engineeringmanagement.procurementmanagement.dto.request.RfqSearchDto;
import com.digigate.engineeringmanagement.procurementmanagement.dto.response.QuoteRequestViewModel;
import com.digigate.engineeringmanagement.procurementmanagement.dto.response.RfqAndPartViewModel;
import com.digigate.engineeringmanagement.procurementmanagement.dto.response.RfqPartViewModel;
import com.digigate.engineeringmanagement.procurementmanagement.dto.response.RfqVendorResponseDto;
import com.digigate.engineeringmanagement.procurementmanagement.entity.PartOrder;
import com.digigate.engineeringmanagement.procurementmanagement.entity.QuoteRequest;
import com.digigate.engineeringmanagement.procurementmanagement.entity.QuoteRequestVendor;
import com.digigate.engineeringmanagement.procurementmanagement.dto.response.*;
import com.digigate.engineeringmanagement.procurementmanagement.entity.*;
import com.digigate.engineeringmanagement.procurementmanagement.repository.QuoteRequestRepository;
import com.digigate.engineeringmanagement.status.service.DemandStatusService;
import com.digigate.engineeringmanagement.status.serviceImpl.DemandStatusServiceImpl;
import com.digigate.engineeringmanagement.storemanagement.constant.RemarkType;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.ApprovalStatus;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.ProcurementRequisition;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.ProcurementRequisitionItem;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.PartRemark;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.RequisitionProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.CommonWorkFlowSearchDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.ApprovalRequestDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.ApprovalStatusDto;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand.ApprovalStatusViewModel;
import com.digigate.engineeringmanagement.storemanagement.service.StoreVoucherTrackingService;
import com.digigate.engineeringmanagement.storemanagement.service.storeconfiguration.PartRemarkService;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.ApprovalStatusService;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.ProcurementRequisitionItemService;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.ProcurementRequisitionService;
import com.digigate.engineeringmanagement.storemanagement.util.SortChanger;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.*;
import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.WORKFLOW_ACTION_ORDER.INITIAL_ORDER;
import static com.digigate.engineeringmanagement.common.constant.ApprovalStatusType.*;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

@Service
public class QuoteRequestService extends AbstractSearchService<
        QuoteRequest,
        RfqRequestDto,
        RfqSearchDto> {
    private final ProcurementRequisitionService requisitionService;
    private final QuoteRequestRepository quoteRequestRepository;
    private final ApprovalStatusService approvalStatusService;
    private final WorkFlowActionService workFlowActionService;
    private final StoreVoucherTrackingService voucherTrackingService;
    private final DemandStatusService demandStatusService;
    private final Helper helper;
    private final WorkFlowUtil workFlowUtil;
    private final QuoteRequestVendorService quoteRequestVendorService;
    private final PartOrderService partOrderService;
    private final ProcurementRequisitionItemService requisitionItemService;
    private final PartRemarkService partRemarkService;
    private final ApprovalEmployeeService approvalEmployeeService;

    private final DemandStatusServiceImpl demandStatusServiceImpl;
    private final VendorQuotationInvoiceDetailService vendorQuotationInvoiceDetailService;

    /**
     * Constructor Parameterized
     *
     * @param quoteRequestRepository    {@link QuoteRequestRepository}
     * @param requisitionService        {@link ProcurementRequisitionService}
     * @param approvalStatusService     {@link ProcurementRequisitionService}
     * @param workFlowActionService     {@link ApprovalEmployeeService}
     * @param voucherTrackingService    {@link StoreVoucherTrackingService}
     * @param helper                    {@link Helper}
     * @param workFlowUtil              {@link WorkFlowUtil}
     * @param quoteRequestVendorService {@link QuoteRequestVendorService}
     * @param partOrderService          {@link PartOrderService}
     * @param partRemarkService
     * @param approvalEmployeeService
     * @param demandStatusServiceImpl
     * @param vendorQuotationInvoiceDetailService
     */
    public QuoteRequestService(
            QuoteRequestRepository quoteRequestRepository,
            ProcurementRequisitionService requisitionService,
            ApprovalStatusService approvalStatusService,
            WorkFlowActionService workFlowActionService,
            StoreVoucherTrackingService voucherTrackingService,
            DemandStatusService demandStatusService, Helper helper,
            WorkFlowUtil workFlowUtil,
            QuoteRequestVendorService quoteRequestVendorService,
            @Lazy PartOrderService partOrderService,
            ProcurementRequisitionItemService requisitionItemService,
            DemandStatusServiceImpl demandStatusServiceImpl,
            @Lazy VendorQuotationInvoiceDetailService vendorQuotationInvoiceDetailService,
            PartRemarkService partRemarkService,
            ApprovalEmployeeService approvalEmployeeService) {
        super(quoteRequestRepository);
        this.quoteRequestRepository = quoteRequestRepository;
        this.requisitionService = requisitionService;
        this.approvalStatusService = approvalStatusService;
        this.workFlowActionService = workFlowActionService;
        this.voucherTrackingService = voucherTrackingService;
        this.demandStatusService = demandStatusService;
        this.helper = helper;
        this.workFlowUtil = workFlowUtil;
        this.quoteRequestVendorService = quoteRequestVendorService;
        this.partOrderService = partOrderService;
        this.requisitionItemService = requisitionItemService;
        this.partRemarkService = partRemarkService;
        this.approvalEmployeeService = approvalEmployeeService;
        this.demandStatusServiceImpl = demandStatusServiceImpl;
        this.vendorQuotationInvoiceDetailService = vendorQuotationInvoiceDetailService;
    }

    /**
     * This method is responsible for create entity
     *
     * @param rfqRequestDto {@link QuoteRequestDto}
     * @return {@link QuoteRequest}
     */
    @Transactional
    @Override
    public QuoteRequest create(RfqRequestDto rfqRequestDto) {
        List<WorkFlowAction> sortedWorkflowActions =
                workFlowActionService.getSortedWorkflowActions(Sort.Direction.ASC);
        WorkFlowAction workFlowAction = workFlowActionService.getByIndex(INITIAL_ORDER, sortedWorkflowActions);
        workFlowUtil.validateWorkflow(helper.getSubModuleItemId(), Collections.singletonList(workFlowAction.getId()));

        QuoteRequest quoteRequest = convertToEntity(rfqRequestDto);

        quoteRequest.setWorkFlowAction(workFlowActionService.
                getByIndex(INITIAL_ORDER + INT_ONE, sortedWorkflowActions));
        quoteRequest.setSubmoduleItemId(helper.getSubModuleItemId());

        quoteRequest = super.saveItem(quoteRequest);

        if(quoteRequest.getRfqType().equals(RfqType.PROCUREMENT)){
            if (Objects.isNull(rfqRequestDto.getPartOrderId())) {
                List<ProcurementRequisitionItem> requisitionItemProjectionList = quoteRequest.getProcurementRequisition().getProcurementRequisitionItems();
                for (ProcurementRequisitionItem procurementRequisitionItem : requisitionItemProjectionList) {
                    demandStatusService.create(
                            procurementRequisitionItem.getDemandItem().getPartId(),
                            quoteRequest.getProcurementRequisition().getId(),
                            quoteRequest.getProcurementRequisition().getStoreDemand().getId(),
                            quoteRequest.getId(),
                            procurementRequisitionItem.getRequisitionQuantity(),
                            quoteRequest.getWorkFlowAction().getId(),
                            VoucherType.RFQ,
                            quoteRequest.getIsActive(),
                            RfqType.PROCUREMENT.name());
                }
            } else {
                List<VendorQuotationInvoiceDetail> vendorQuotationInvoiceDetailList = quoteRequest.getPartOrder().getPartOrderItemList().stream()
                        .map(PartOrderItem::getIqItem).collect(Collectors.toList());

                for (VendorQuotationInvoiceDetail vendorQuotationInvoiceDetail : vendorQuotationInvoiceDetailList) {
                    demandStatusService.create(

                            Objects.nonNull(vendorQuotationInvoiceDetail.getAlternatePart()) ? vendorQuotationInvoiceDetail.getAlternatePart().getId() :
                                    vendorQuotationInvoiceDetail.getRequisitionItem().getDemandItem().getPart().getId(),
                            rfqRequestDto.getPartOrderId(),
                            vendorQuotationInvoiceDetail.getRequisitionItem().getDemandItem().getStoreDemand().getId(),
                            quoteRequest.getId(),
                            vendorQuotationInvoiceDetail.getPartQuantity(),
                            workFlowActionService.
                                    getByIndex(INITIAL_ORDER + INT_ONE, sortedWorkflowActions).getId(),
                            VoucherType.RFQ,
                            quoteRequest.getIsActive(),
                            RfqType.PROCUREMENT.name());
                }
            }
        }
        if(quoteRequest.getRfqType().equals(RfqType.LOGISTIC) && Objects.nonNull(quoteRequest.getPartOrder())){
            PoResponseDto poResponseDto = partOrderService.getSingle(rfqRequestDto.getPartOrderId());

            for (PoItemResponseDto poItemResponseDto : poResponseDto.getPoItemResponseDtoList()) {
                demandStatusService.create(
                        Objects.nonNull(poItemResponseDto.getVendorQuotationInvoiceDetails().getAlternatePartId()) ?
                                poItemResponseDto.getVendorQuotationInvoiceDetails().getAlternatePartId()
                                :poItemResponseDto.getVendorQuotationInvoiceDetails().getPartId(),
                        rfqRequestDto.getPartOrderId(),
                        vendorQuotationInvoiceDetailService.findById(poResponseDto.getVendorQuotationViewModel()
                                .getVendorQuotationDetails().get(FIRST_INDEX).getId()).getRequisitionItem().getDemandItem().getStoreDemand().getId(),
                        quoteRequest.getId(),
                        poItemResponseDto.getQuantity(),
                        workFlowActionService.
                                getByIndex(INITIAL_ORDER + INT_ONE, sortedWorkflowActions).getId(),
                        VoucherType.RFQ,
                        quoteRequest.getIsActive(),
                        RfqType.LOGISTIC.name());
            }
        }
        quoteRequestVendorService.createOrUpdate(rfqRequestDto.getQuoteRequestVendorModelList(), quoteRequest, null);
        approvalStatusService.create(ApprovalStatusDto.of(quoteRequest.getId(), workflowType(rfqRequestDto.getRfqType()), workFlowAction));
        return quoteRequest;
    }

    @Transactional
    public Pair<QuoteRequest, List<QuoteRequestVendor>> create(RfqRequestDto rfqRequestDto, InputType inputType) {
        QuoteRequest quoteRequest = convertToEntity(rfqRequestDto);
        quoteRequest.setRfqNo(INVISIBLE + ZonedDateTime.now().toInstant().toEpochMilli());
        quoteRequest.setInputType(inputType);

        WorkFlowAction workFlowAction = workFlowActionService.findFinalAction();
        quoteRequest.setWorkFlowAction(workFlowAction);
        quoteRequest.setSubmoduleItemId(helper.getSubModuleItemId());

        quoteRequest = super.saveItem(quoteRequest);

        if(quoteRequest.getRfqType().equals(RfqType.PROCUREMENT)){
                List<ProcurementRequisitionItem> requisitionItemProjectionList = quoteRequest.getProcurementRequisition().getProcurementRequisitionItems();
                for (ProcurementRequisitionItem procurementRequisitionItem : requisitionItemProjectionList) {
                    demandStatusService.create(
                            procurementRequisitionItem.getDemandItem().getPartId(),
                            quoteRequest.getProcurementRequisition().getId(),
                            quoteRequest.getProcurementRequisition().getStoreDemand().getId(),
                            quoteRequest.getId(),
                            procurementRequisitionItem.getRequisitionQuantity(),
                            quoteRequest.getWorkFlowAction().getId(),
                            VoucherType.RFQ,
                            quoteRequest.getIsActive(),
                            RfqType.PROCUREMENT.name());
                }
            }
        if(quoteRequest.getRfqType().equals(RfqType.LOGISTIC) && Objects.nonNull(quoteRequest.getPartOrder())){
            List<VendorQuotationInvoiceDetail> vendorQuotationInvoiceDetailList = quoteRequest.getPartOrder().getPartOrderItemList().stream().map(PartOrderItem::getIqItem).collect(Collectors.toList());
            for (VendorQuotationInvoiceDetail vendorQuotationInvoiceDetail : vendorQuotationInvoiceDetailList) {
                demandStatusService.create(
                        Objects.nonNull(vendorQuotationInvoiceDetail.getAlternatePart())?vendorQuotationInvoiceDetail.getAlternatePart().getId():
                                vendorQuotationInvoiceDetail.getRequisitionItem().getDemandItem().getPart().getId(),
                        vendorQuotationInvoiceDetail.getRequisitionItem().getProcurementRequisition().getId(),
                        vendorQuotationInvoiceDetail.getRequisitionItem().getDemandItem().getStoreDemand().getId(),
                        quoteRequest.getId(),
                        vendorQuotationInvoiceDetail.getRequisitionItem().getRequisitionQuantity(),
                        quoteRequest.getWorkFlowAction().getId(),
                        VoucherType.RFQ,
                        quoteRequest.getIsActive(),
                        RfqType.LOGISTIC.name());
            }
        }

        approvalStatusService.create(ApprovalStatusDto.of(quoteRequest.getId(), workflowType(rfqRequestDto.getRfqType()), workFlowAction));
        List<QuoteRequestVendor> quoteRequestVendors = quoteRequestVendorService.createOrUpdate(
                rfqRequestDto.getQuoteRequestVendorModelList(), quoteRequest, null);

        return Pair.of(quoteRequest, quoteRequestVendors);
    }

    /**
     * This method is responsible for update QuoteRequest
     *
     * @param rfqRequestDto {@link QuoteRequestDto}
     * @param id            {@link Long}
     * @return {@link QuoteRequest}
     */
    @Transactional
    @Override
    public QuoteRequest update(RfqRequestDto rfqRequestDto, Long id) {
        QuoteRequest quoteRequest = findByIdUnfiltered(id);
        Long subModuleItemId = helper.getSubModuleItemId();

        workFlowUtil.validateUpdatability(quoteRequest.getWorkFlowActionId());

        WorkFlowAction currentAction = quoteRequest.getWorkFlowAction();
        workFlowUtil.validateWorkflow(subModuleItemId, Arrays.asList(currentAction.getId(),
                workFlowActionService.getNavigatedAction(false, currentAction).getId()));

        quoteRequestVendorService.createOrUpdate(rfqRequestDto.getQuoteRequestVendorModelList(), quoteRequest, id);

        boolean isDeleted = true;
        if (quoteRequest.getRfqType().equals(RfqType.PROCUREMENT)) {

            List<ProcurementRequisitionItem> requisitionItemProjectionList = quoteRequest.getProcurementRequisition().getProcurementRequisitionItems();
            for (ProcurementRequisitionItem procurementRequisitionItem : requisitionItemProjectionList) {
                if (isDeleted) {
                    demandStatusServiceImpl.deleteAllDemandStatus(
                            quoteRequest.getProcurementRequisition().getStoreDemand().getId(),
                            quoteRequest.getId(),
                            VoucherType.RFQ);
                    isDeleted = false;
                }
                demandStatusService.entityUpdate(
                        procurementRequisitionItem.getDemandItem().getPartId(),
                        quoteRequest.getProcurementRequisition().getId(),
                        quoteRequest.getProcurementRequisition().getStoreDemand().getId(),
                        quoteRequest.getId(),
                        procurementRequisitionItem.getRequisitionQuantity(),
                        quoteRequest.getWorkFlowAction().getId(),
                        VoucherType.RFQ,
                        quoteRequest.getIsActive(),
                        RfqType.PROCUREMENT.name());
            }
        }
        if (quoteRequest.getRfqType().equals(RfqType.LOGISTIC) && Objects.nonNull(quoteRequest.getPartOrder())) {

            List<VendorQuotationInvoiceDetail> vendorQuotationInvoiceDetailList = quoteRequest.getPartOrder().getPartOrderItemList().stream().map(PartOrderItem::getIqItem).collect(Collectors.toList());
            for (VendorQuotationInvoiceDetail vendorQuotationInvoiceDetail : vendorQuotationInvoiceDetailList) {
                if (isDeleted) {
                    demandStatusServiceImpl.deleteAllDemandStatus(
                            vendorQuotationInvoiceDetail.getRequisitionItem().getDemandItem().getStoreDemand().getId(),
                            quoteRequest.getId(),
                            VoucherType.RFQ);
                    isDeleted = false;
                }
                demandStatusService.entityUpdate(
                        Objects.nonNull(vendorQuotationInvoiceDetail.getAlternatePart())?vendorQuotationInvoiceDetail.getAlternatePart().getId():
                                vendorQuotationInvoiceDetail.getRequisitionItem().getDemandItem().getPart().getId(),
                        vendorQuotationInvoiceDetail.getRequisitionItem().getProcurementRequisition().getId(),
                        vendorQuotationInvoiceDetail.getRequisitionItem().getDemandItem().getStoreDemand().getId(),
                        quoteRequest.getId(),
                        vendorQuotationInvoiceDetail.getPartQuantity(),
                        quoteRequest.getWorkFlowAction().getId(),
                        VoucherType.RFQ,
                        quoteRequest.getIsActive(),
                        RfqType.LOGISTIC.name());
            }
        }

        return quoteRequest;
    }

    @Transactional
    public Pair<QuoteRequest, List<QuoteRequestVendor>> update(RfqRequestDto rfqRequestDto, InputType inputType, Long poId) {
        QuoteRequest quoteRequest = quoteRequestRepository.findQuoteRequestByPartOrderId(poId);
        quoteRequest = updateEntity(rfqRequestDto, quoteRequest);
        quoteRequest.setInputType(inputType);

        quoteRequest = super.saveItem(quoteRequest);

        List<QuoteRequestVendor> quoteRequestVendors = quoteRequestVendorService.createOrUpdate(
                rfqRequestDto.getQuoteRequestVendorModelList(), quoteRequest, quoteRequest.getId());

        return Pair.of(quoteRequest, quoteRequestVendors);
    }

    /**
     * This method is responsible for return a sing quote request with vendors
     *
     * @param rfqId {@link Long}
     * @return {@link RfqVendorResponseDto}
     */
    @Override
    public RfqAndPartViewModel getSingle(Long rfqId) {
        List<WorkFlowActionProjection> approvedActionsForUser = approvalEmployeeService
                .findApprovedActionsForUser(helper.getSubModuleItemId(), Helper.getAuthUserId());
        QuoteRequest quoteRequest = findByIdUnfiltered(rfqId);
        QuoteRequestViewModel quoteRequestViewModel = convertToResponseDto(quoteRequest);
        List<QuoteRequestVendor> quoteRequestVendorList = quoteRequestVendorService.findByQuoteRequestId(rfqId);
        RfqVendorResponseDto rfqVendorResponseDto = quoteRequestVendorService.prepareQuoteRequestVendorViewModel(
                quoteRequestVendorList, quoteRequestViewModel);
        prepareWorkFlowAndApprovalRemarksInSingleView(rfqVendorResponseDto, approvedActionsForUser);
        return RfqAndPartViewModel.of(rfqVendorResponseDto, getAllRfqPart(quoteRequest));
    }

    private void prepareWorkFlowAndApprovalRemarksInSingleView(RfqVendorResponseDto rfqVendorResponseDto, List<WorkFlowActionProjection> approvedActionsForUser) {
        WorkFlowDto workFlowDto = workFlowUtil.prepareResponseData(Set.of(rfqVendorResponseDto.getId()), approvedActionsForUser, workflowType(rfqVendorResponseDto.getRfqType()));
        List<ApprovalStatus> approvalStatuses = workFlowDto.getStatusMap().get(rfqVendorResponseDto.getId());
        Map<Long, ApprovalStatus> workFlowActionMap = approvalStatuses.stream().collect(Collectors.toMap(ApprovalStatus::getWorkFlowActionId, Function.identity()));
        List<PartRemark> partRemarkList = partRemarkService.findByParentIdAndRemarkType(Set.of(rfqVendorResponseDto.getId()), getRemarkType(rfqVendorResponseDto.getRfqType())); //approval  remarks

        rfqVendorResponseDto.setApprovalStatuses(approvalStatuses.stream().map(approvalStatus ->
                        ApprovalStatusViewModel.from(approvalStatus, workFlowDto.getNamesFromApprovalStatuses()))
                .collect(Collectors.toMap(ApprovalStatusViewModel::getWorkFlowActionId,
                        Function.identity(), (a, b) -> b)));

        if (CollectionUtils.isNotEmpty(partRemarkList)) {
            rfqVendorResponseDto.setApprovalRemarksResponseDtoList(partRemarkList.stream().map(partRemark ->
                    partRemarkService.prepareApprovalRemarkResponse(partRemark, workFlowActionMap, workFlowDto.getNamesFromApprovalStatuses())).collect(Collectors.toList()));
        }
    }

    public Optional<QuoteRequestProjection> findQuoteRequestById(Long id) {
        Optional<QuoteRequestProjection> quoteRequestProjection = quoteRequestRepository.findQuoteRequestById(id);
        if (quoteRequestProjection.isEmpty()) {
            throw EngineeringManagementServerException.notFound(
                    Helper.createDynamicCode(ErrorId.DATA_NOT_FOUND_DYNAMIC, ApplicationConstant.RFQ));
        }
        return quoteRequestProjection;
    }

    public List<QuoteRequestProjection> findQuoteRequestByIdIn(Set<Long> idList) {
        return quoteRequestRepository.findQuoteRequestByIdIn(idList);
    }

    /**
     * This method is responsible for checking requisition as parent
     *
     * @param id {@link ProcurementRequisition}
     * @return responding primitive 0/1
     */
    public boolean isExistRequisitionInQuteRequest(Long id) {
        return quoteRequestRepository.existsByRequisitionIdAndIsActiveTrue(id);
    }

    /**
     * This method is responsible for search by specification
     *
     * @param dto      {@link CommonWorkFlowSearchDto}
     * @param pageable {@link Pageable}
     * @return responding {@link PageData}
     */
    @Override
    public PageData search(RfqSearchDto dto, Pageable pageable) {
        pageable = SortChanger.descendingSortByCreatedAt(pageable);
        Page<QuoteRequest> pageData;

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
                .model(getAllResponseDto(pageData.getContent(), approvedActionForUser, dto.getRfqType()))
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    private Page<QuoteRequest> customBuildSpecification(RfqSearchDto searchDto,
                                                        Pageable pageable,
                                                        Set<Long> approvedIds,
                                                        Boolean isRejected) {

        CustomSpecification<QuoteRequest> customSpecification = new CustomSpecification<>();
        Specification<QuoteRequest> specification = Specification.where(
                customSpecification.equalSpecificationAtRoot(searchDto.getIsActive(), IS_ACTIVE_FIELD)
                        .and(customSpecification.equalSpecificationAtRoot(isRejected, IS_REJECTED_FIELD))
                        .and(customSpecification.inSpecificationAtRoot(approvedIds, WORKFLOW_ACTION_ID))
                        .and(customSpecification.equalSpecificationAtRoot(searchDto.getOrderType(), ORDER_TYPE))
                        .and(customSpecification.equalSpecificationAtRoot(searchDto.getRfqType(), RFQ_TYPE))
                        .and(customSpecification.likeSpecificationAtRoot(searchDto.getQuery(), RFQ_NO))
                        .and(customSpecification.likeSpecificationAtRoot(VoucherType.RFQ.name(), RFQ_NO))
        );

        return quoteRequestRepository.findAll(specification, pageable);
    }

    /**
     * This method will convert dto to entity
     *
     * @param rfqRequestDto {@link RfqRequestDto}
     * @return {@link QuoteRequest}
     */
    @Override
    protected QuoteRequest convertToEntity(RfqRequestDto rfqRequestDto) {
        return populateDtoToEntity(rfqRequestDto, new QuoteRequest());
    }

    @Override
    protected QuoteRequest updateEntity(RfqRequestDto rfqRequestDto, QuoteRequest quoteRequest) {
        return populateDtoToEntity(rfqRequestDto, quoteRequest);
    }

    private QuoteRequest populateDtoToEntity(RfqRequestDto rfqRequestDto, QuoteRequest quoteRequest) {

        /** This check for RO Request */
        if (rfqRequestDto.getRfqType() == RfqType.PROCUREMENT
                && Objects.nonNull(rfqRequestDto.getPartOrderId())) {
            quoteRequest.setProcurementRequisition(requisitionService.findRequisitionByPoId(rfqRequestDto.getPartOrderId()));
        }

        if (Objects.nonNull(rfqRequestDto.getRequisition())) {
            quoteRequest.setProcurementRequisition(rfqRequestDto.getRequisition());
        } else if (Objects.nonNull(rfqRequestDto.getRequisitionId())) {
            ProcurementRequisition procurementRequisition = requisitionService.findById(rfqRequestDto.getRequisitionId());
            quoteRequest.setProcurementRequisition(procurementRequisition);

            /** Setting rfq pattern */
            quoteRequest.setRfqNo(voucherTrackingService.generateUniqueVoucherNo(rfqRequestDto.getRequisitionId(),
                    VoucherType.RFQ, procurementRequisition.getVoucherNo()));
        }

        if (Objects.nonNull(rfqRequestDto.getPartOrder())) {
            quoteRequest.setPartOrder(rfqRequestDto.getPartOrder());
        } else if (Objects.nonNull(rfqRequestDto.getPartOrderId())) {
            PartOrder partOrder = partOrderService.findById(rfqRequestDto.getPartOrderId());
            quoteRequest.setPartOrder(partOrder);

            /** Setting rfq pattern */
            quoteRequest.setRfqNo(voucherTrackingService.generateUniqueVoucherNo(rfqRequestDto.getPartOrderId(),
                    VoucherType.RFQ, partOrder.getVoucherNo()));
        }

        if (Objects.isNull(rfqRequestDto.getRequisitionId())
                && Objects.isNull(rfqRequestDto.getPartOrderId())
                && Objects.isNull(rfqRequestDto.getRequisition())
                && Objects.isNull(rfqRequestDto.getPartOrder())) {
            throw EngineeringManagementServerException.badRequest(ErrorId.AT_LEAST_ONE_ID_REQUIRED);
        }
        quoteRequest.setRfqType(rfqRequestDto.getRfqType());

        return quoteRequest;
    }

    private List<RfqPartViewModel> populateToPartView(Long requisitionId) {
        return requisitionItemService.getRfqPartViewModelLIst(requisitionId);
    }

    private List<RfqPartViewModel> populateToPartView(Long partOrderId, Long overload) {
        return partOrderService.getRfqPartViewModelLIst(partOrderId);
    }

    /**
     * This method is responsible for getting all data in an optimized way!
     *
     * @param quoteRequestList {@link QuoteRequest}
     * @return responding list of {@link QuoteRequestViewModel}
     */
    private List<RfqVendorResponseDto> getAllResponseDto(List<QuoteRequest> quoteRequestList,
                                                         List<WorkFlowActionProjection> approvedActions, RfqType rfqType) {
        List<RfqVendorResponseDto> rfqVendorResponseDtoList = new ArrayList<>();
        Set<Long> returnIds = quoteRequestList.stream().map(QuoteRequest::getId).collect(Collectors.toSet());
        WorkFlowDto workFlowDto = workFlowUtil.prepareResponseData(returnIds, approvedActions, workflowType(rfqType));

        Map<Long, List<PartRemark>> partRemarkList = partRemarkService.findByParentIdAndRemarkType(returnIds, getRemarkType(rfqType)).stream()
                .collect(Collectors.groupingBy(PartRemark::getParentId)); //approval  remarks

        for (QuoteRequest quoteRequest : quoteRequestList) {
            QuoteRequestViewModel viewModel = new QuoteRequestViewModel();
            if (Objects.nonNull(quoteRequest.getId())) {
                viewModel = convertToResponseDto(quoteRequest);
            }
            RfqVendorResponseDto rfqVendorResponseDto = quoteRequestVendorService.getAllQuoteRequestVendorByRfq(viewModel);
            setWorkFlowResponse(rfqVendorResponseDto, quoteRequest, workFlowDto, partRemarkList.get(quoteRequest.getId()));
            rfqVendorResponseDtoList.add(rfqVendorResponseDto);
        }
        return rfqVendorResponseDtoList;
    }

    /**
     * This method is responsible for building specification for search
     *
     * @param searchDto {@link IdQuerySearchDto}
     * @return responding {@link Specification}
     */
    @Override
    protected Specification<QuoteRequest> buildSpecification(RfqSearchDto searchDto) {
        CustomSpecification<QuoteRequest> quoteRequestCustomSpecification = new CustomSpecification<>();
        return Specification
                .where(quoteRequestCustomSpecification.likeSpecificationAtRoot(searchDto.getQuery(), RFQ_NO)
                        .and(quoteRequestCustomSpecification.likeSpecificationAtRoot(VoucherType.RFQ.name(), RFQ_NO)));
    }

    @Override
    protected QuoteRequestViewModel convertToResponseDto(QuoteRequest quoteRequest) {
        QuoteRequestViewModel quoteRequestViewModel = new QuoteRequestViewModel();
        quoteRequestViewModel.setId(quoteRequest.getId());
        quoteRequestViewModel.setRfqNo(quoteRequest.getRfqNo());
        quoteRequestViewModel.setRfqDate(quoteRequest.getCreatedAt());
        if (Objects.nonNull(quoteRequest.getRequisitionId())) {
            RequisitionProjection requisitionProjection = requisitionService.findRequisitionById(quoteRequest.getRequisitionId());
            quoteRequestViewModel.setRequisitionId(requisitionProjection.getId());
            quoteRequestViewModel.setVoucherNo(requisitionProjection.getVoucherNo());
        }
        if (Objects.nonNull(quoteRequest.getPartOrderId())) {
            PartOrder partOrder = partOrderService.findById(quoteRequest.getPartOrderId());
            quoteRequestViewModel.setPartOrderId(partOrder.getId());
            quoteRequestViewModel.setOrderNo(partOrder.getOrderNo());
        }

        quoteRequestViewModel.setRfqType(quoteRequest.getRfqType());
        quoteRequestViewModel.setInputType(quoteRequest.getInputType());
        quoteRequestViewModel.setRejectedDesc(quoteRequest.getRejectedDesc());
        quoteRequestViewModel.setIsRejected(quoteRequest.getIsRejected());
        return quoteRequestViewModel;
    }

    /**
     * This method will set workflow response to RFQVendorResponse
     *
     * @param rfqVendorResponseDto {@link RfqVendorResponseDto}
     * @param quoteRequest         {@link QuoteRequest}
     * @param workFlowDto          {@link WorkFlowDto}
     * @param partRemarks
     */
    private void setWorkFlowResponse(RfqVendorResponseDto rfqVendorResponseDto, QuoteRequest quoteRequest,
                                     WorkFlowDto workFlowDto, List<PartRemark> partRemarks) {
        List<ApprovalStatus> approvalStatuses = workFlowDto.getStatusMap()
                .getOrDefault(quoteRequest.getId(), new ArrayList<>());
        Map<Long, ApprovalStatus> workFlowActionMap = approvalStatuses.stream().collect(Collectors.toMap(ApprovalStatus::getWorkFlowActionId, Function.identity()));
        WorkFlowAction workFlowAction = workFlowDto.getWorkFlowActionMap().get(quoteRequest.getWorkFlowActionId());
        rfqVendorResponseDto.setWorkflowName(workFlowAction.getName());
        rfqVendorResponseDto.setWorkflowOrder(workFlowAction.getOrderNumber());
        rfqVendorResponseDto.setActionEnabled(workFlowDto.getActionableIds().contains(quoteRequest.getWorkFlowActionId()));
        rfqVendorResponseDto.setEditable(workFlowDto.getEditableIds().contains(quoteRequest.getWorkFlowActionId()));
        rfqVendorResponseDto.setWorkFlowActionId(quoteRequest.getWorkFlowActionId());
        rfqVendorResponseDto.setApprovalStatuses(approvalStatuses.stream().map(approvalStatus ->
                        ApprovalStatusViewModel.from(approvalStatus, workFlowDto.getNamesFromApprovalStatuses()))
                .collect(Collectors.toMap(ApprovalStatusViewModel::getWorkFlowActionId,
                        Function.identity(), (a, b) -> b)));

        if (CollectionUtils.isNotEmpty(partRemarks)) {
            rfqVendorResponseDto.setApprovalRemarksResponseDtoList(partRemarks.stream().map(partRemark ->
                    partRemarkService.prepareApprovalRemarkResponse(partRemark, workFlowActionMap, workFlowDto.getNamesFromApprovalStatuses())).collect(Collectors.toList()));
        }
    }

    /**
     * This method will make decision of approval
     *
     * @param id                 {@link Long}
     * @param approvalRequestDto {@link ApprovalRequestDto}
     */
    @Transactional
    public void makeDecision(Long id, ApprovalRequestDto approvalRequestDto, RfqType rfqType) {
        QuoteRequest quoteRequest = findById(id);
        Long subModuleItemId = helper.getSubModuleItemId();
        workFlowUtil.validateUpdatability(quoteRequest.getWorkFlowActionId());
        workFlowUtil.validateWorkflow(subModuleItemId, Collections.singletonList(quoteRequest.getWorkFlowActionId()));
        if (approvalRequestDto.getApprove() == TRUE) {
            approvalStatusService.create(ApprovalStatusDto.of(quoteRequest.getId(), workflowType(rfqType), quoteRequest.getWorkFlowAction()));
            if (StringUtils.isEmpty(approvalRequestDto.getApprovalDesc())) {
                throw EngineeringManagementServerException.notFound(ErrorId.APPROVAL_REMARK_CAN_NOT_BE_EMPTY);
            }
            partRemarkService.saveApproveRemark(quoteRequest.getId(), quoteRequest.getWorkFlowAction().getId(),
                    getRemarkType(quoteRequest.getRfqType()), approvalRequestDto.getApprovalDesc());

            quoteRequest.setWorkFlowAction(workFlowActionService.getNavigatedAction(true, quoteRequest.getWorkFlowAction()));
        } else {
            if (StringUtils.isEmpty(approvalRequestDto.getRejectedDesc())) {
                throw EngineeringManagementServerException.notFound
                        (ErrorId.REJECTED_DESCRIPTION_CAN_NOT_BE_EMPTY);
            }
            quoteRequest.setIsRejected(true);
            quoteRequest.setRejectedDesc(approvalRequestDto.getRejectedDesc());
        }
        super.saveItem(quoteRequest);

        if (quoteRequest.getRfqType().equals(RfqType.PROCUREMENT)) {
            if(Objects.nonNull(quoteRequest.getPartOrder())){
                List<VendorQuotationInvoiceDetail> vendorQuotationInvoiceDetailList = quoteRequest.getPartOrder().getPartOrderItemList().stream().map(PartOrderItem::getIqItem).collect(Collectors.toList());
                for (VendorQuotationInvoiceDetail vendorQuotationInvoiceDetail : vendorQuotationInvoiceDetailList) {
                    demandStatusService.update(
                            Objects.nonNull(vendorQuotationInvoiceDetail.getAlternatePart())?vendorQuotationInvoiceDetail.getAlternatePart().getId()
                                    :vendorQuotationInvoiceDetail.getRequisitionItem().getDemandItem().getPart().getId(),
                            quoteRequest.getId(),
                            quoteRequest.getWorkFlowAction().getId(),
                            quoteRequest.getIsRejected(),
                            VoucherType.RFQ,
                            RfqType.PROCUREMENT.name());
                }
            }else {

                List<ProcurementRequisitionItem> requisitionItemProjectionList = quoteRequest.getProcurementRequisition().getProcurementRequisitionItems();
                for (ProcurementRequisitionItem procurementRequisitionItem : requisitionItemProjectionList) {
                    demandStatusService.update(
                            procurementRequisitionItem.getDemandItem().getPartId(),
                            quoteRequest.getId(),
                            quoteRequest.getWorkFlowAction().getId(),
                            quoteRequest.getIsRejected(),
                            VoucherType.RFQ,
                            RfqType.PROCUREMENT.name());
                }
            }
        }
        if (quoteRequest.getRfqType().equals(RfqType.LOGISTIC) && Objects.nonNull(quoteRequest.getPartOrder())) {
            List<VendorQuotationInvoiceDetail> vendorQuotationInvoiceDetailList = quoteRequest.getPartOrder().getPartOrderItemList().stream().map(PartOrderItem::getIqItem).collect(Collectors.toList());
            for (VendorQuotationInvoiceDetail vendorQuotationInvoiceDetail : vendorQuotationInvoiceDetailList) {
                demandStatusService.update(
                        Objects.nonNull(vendorQuotationInvoiceDetail.getAlternatePart())?vendorQuotationInvoiceDetail.getAlternatePart().getId()
                                :vendorQuotationInvoiceDetail.getRequisitionItem().getDemandItem().getPart().getId(),
                        quoteRequest.getId(),
                        quoteRequest.getWorkFlowAction().getId(),
                        quoteRequest.getIsRejected(),
                        VoucherType.RFQ,
                        RfqType.LOGISTIC.name());
            }
        }
    }

    public void updateActiveStatus(Long id, Boolean isActive, RfqType rfqType) {
        QuoteRequest quoteRequest = findByIdUnfiltered(id);
        workFlowUtil.validateUpdatability(quoteRequest.getWorkFlowActionId());
        quoteRequest.setIsActive(isActive);
        if (isActive == TRUE) {
            WorkFlowAction workFlowAction = workFlowActionService.getNavigatedAction(false, quoteRequest.getWorkFlowAction());
            workFlowUtil.validateWorkflow(helper.getSubModuleItemId(), List.of(quoteRequest.getWorkFlowActionId(), workFlowAction.getId()));
            partRemarkService.revertPreviousActionRemarks(quoteRequest.getId(), workFlowAction, getRemarkType(quoteRequest.getRfqType()));
            quoteRequest.setWorkFlowAction(workFlowUtil.revertAndFindPrevAction(quoteRequest.getWorkFlowAction(),
                    workflowType(rfqType), quoteRequest.getId()));
            quoteRequest.setIsRejected(FALSE);
            setDemandStatusIsActiveUpdatedValue(quoteRequest);
            setDemandStatusRejectedUpdatedValue(quoteRequest);

        } else {
            workFlowUtil.validateWorkflow(helper.getSubModuleItemId(), Collections.singletonList(quoteRequest.getWorkFlowAction().getId()));
            setDemandStatusIsActiveUpdatedValue(quoteRequest);
        }
        saveItem(quoteRequest);
    }

    private void setDemandStatusIsActiveUpdatedValue(QuoteRequest quoteRequest) {
        if (quoteRequest.getRfqType().equals(RfqType.PROCUREMENT)) {
            if(Objects.nonNull(quoteRequest.getPartOrder())){
                List<VendorQuotationInvoiceDetail> vendorQuotationInvoiceDetailList = quoteRequest.getPartOrder().getPartOrderItemList().stream().map(PartOrderItem::getIqItem).collect(Collectors.toList());
                for (VendorQuotationInvoiceDetail vendorQuotationInvoiceDetail : vendorQuotationInvoiceDetailList) {
                    demandStatusService.updateActiveStatus(
                            vendorQuotationInvoiceDetail.getRequisitionItem().getDemandItem().getStoreDemand().getId(),
                            quoteRequest.getId(),
                            Objects.nonNull(vendorQuotationInvoiceDetail.getAlternatePart())?vendorQuotationInvoiceDetail.getAlternatePart().getId()
                                    :vendorQuotationInvoiceDetail.getRequisitionItem().getDemandItem().getPart().getId(),
                            VoucherType.RFQ,
                            quoteRequest.getIsActive(),
                            quoteRequest.getWorkFlowAction().getId());
                }
            }else{
                List<ProcurementRequisitionItem> requisitionItemProjectionList = quoteRequest.getProcurementRequisition().getProcurementRequisitionItems();
                for (ProcurementRequisitionItem procurementRequisitionItem : requisitionItemProjectionList) {
                    demandStatusService.updateActiveStatus(
                            procurementRequisitionItem.getDemandItem().getStoreDemand().getId(),
                            quoteRequest.getId(),
                            procurementRequisitionItem.getDemandItem().getPart().getId(),
                            VoucherType.RFQ,
                            quoteRequest.getIsActive(),
                            quoteRequest.getWorkFlowAction().getId());
                }
            }

        }
        if (quoteRequest.getRfqType().equals(RfqType.LOGISTIC) && Objects.nonNull(quoteRequest.getPartOrder())) {
            List<VendorQuotationInvoiceDetail> vendorQuotationInvoiceDetailList = quoteRequest.getPartOrder().getPartOrderItemList().stream().map(PartOrderItem::getIqItem).collect(Collectors.toList());
            for (VendorQuotationInvoiceDetail vendorQuotationInvoiceDetail : vendorQuotationInvoiceDetailList) {
                demandStatusService.updateActiveStatus(
                        vendorQuotationInvoiceDetail.getRequisitionItem().getDemandItem().getStoreDemand().getId(),
                        quoteRequest.getId(),
                        Objects.nonNull(vendorQuotationInvoiceDetail.getAlternatePart())?vendorQuotationInvoiceDetail.getAlternatePart().getId()
                                :vendorQuotationInvoiceDetail.getRequisitionItem().getDemandItem().getPart().getId(),
                        VoucherType.RFQ,
                        quoteRequest.getIsActive(),
                        quoteRequest.getWorkFlowAction().getId());
            }
        }
    }

    private void setDemandStatusRejectedUpdatedValue(QuoteRequest quoteRequest) {
        if (quoteRequest.getRfqType().equals(RfqType.PROCUREMENT)) {
            if (Objects.nonNull(quoteRequest.getPartOrder())) {
                List<VendorQuotationInvoiceDetail> vendorQuotationInvoiceDetailList = quoteRequest.getPartOrder().getPartOrderItemList().stream().map(PartOrderItem::getIqItem).collect(Collectors.toList());
                for (VendorQuotationInvoiceDetail vendorQuotationInvoiceDetail : vendorQuotationInvoiceDetailList) {
                    demandStatusService.updateRejectedStatus(
                            vendorQuotationInvoiceDetail.getRequisitionItem().getDemandItem().getStoreDemand().getId(),
                            quoteRequest.getId(),
                            Objects.nonNull(vendorQuotationInvoiceDetail.getAlternatePart())?vendorQuotationInvoiceDetail.getAlternatePart().getId()
                                    :vendorQuotationInvoiceDetail.getRequisitionItem().getDemandItem().getPart().getId(),

                            VoucherType.RFQ,
                            quoteRequest.getIsRejected());
                }
            } else {
                List<ProcurementRequisitionItem> requisitionItemProjectionList = quoteRequest.getProcurementRequisition().getProcurementRequisitionItems();
                for (ProcurementRequisitionItem procurementRequisitionItem : requisitionItemProjectionList) {
                    demandStatusService.updateRejectedStatus(
                            procurementRequisitionItem.getDemandItem().getStoreDemand().getId(),
                            quoteRequest.getId(),
                            procurementRequisitionItem.getDemandItem().getPart().getId(),
                            VoucherType.RFQ,
                            quoteRequest.getIsRejected());
                }
            }
        }
        if (quoteRequest.getRfqType().equals(RfqType.LOGISTIC) && Objects.nonNull(quoteRequest.getPartOrder())) {
            List<VendorQuotationInvoiceDetail> vendorQuotationInvoiceDetailList = quoteRequest.getPartOrder().getPartOrderItemList().stream().map(e->e.getIqItem()).collect(Collectors.toList());
            for (VendorQuotationInvoiceDetail vendorQuotationInvoiceDetail : vendorQuotationInvoiceDetailList) {
                demandStatusService.updateRejectedStatus(
                        vendorQuotationInvoiceDetail.getRequisitionItem().getDemandItem().getStoreDemand().getId(),
                        quoteRequest.getId(),
                        Objects.nonNull(vendorQuotationInvoiceDetail.getAlternatePart())?vendorQuotationInvoiceDetail.getAlternatePart().getId()
                                :vendorQuotationInvoiceDetail.getRequisitionItem().getDemandItem().getPart().getId(),

                        VoucherType.RFQ,
                        quoteRequest.getIsRejected());
            }
        }
    }

    private ApprovalStatusType workflowType(RfqType rfqType) {
        return rfqType == RfqType.PROCUREMENT ? PROCUREMENT_RFQ : LOGISTIC_RFQ;
    }

    private RemarkType getRemarkType(RfqType rfqType) {
        return rfqType == RfqType.PROCUREMENT ? RemarkType.PROCUREMENT_RFQ_APPROVAL_REMARK : RemarkType.LOGISTIC_RFQ_APPROVAL_REMARK;
    }


    private List<RfqPartViewModel> getAllRfqPart(QuoteRequest quoteRequest) {
        if (quoteRequest.getRfqType() == RfqType.PROCUREMENT) {
            return populateToPartView(quoteRequest.getRequisitionId());
        }

        return populateToPartView(quoteRequest.getPartOrderId(), OVERLOAD);
    }
}
