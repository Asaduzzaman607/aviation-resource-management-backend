package com.digigate.engineeringmanagement.procurementmanagement.service;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.VoucherType;
import com.digigate.engineeringmanagement.common.entity.User;
import com.digigate.engineeringmanagement.common.payload.request.search.WorkFlowDto;
import com.digigate.engineeringmanagement.common.payload.response.ApprovalRemarksResponseDto;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.service.UserService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.common.util.Helper;
import com.digigate.engineeringmanagement.common.util.WorkFlowUtil;
import com.digigate.engineeringmanagement.configurationmanagement.dto.projection.VendorProjection;
import com.digigate.engineeringmanagement.configurationmanagement.dto.projection.WorkFlowActionProjection;
import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.WorkFlowAction;
import com.digigate.engineeringmanagement.configurationmanagement.service.administration.ApprovalEmployeeService;
import com.digigate.engineeringmanagement.configurationmanagement.service.administration.WorkFlowActionService;
import com.digigate.engineeringmanagement.planning.entity.Part;
import com.digigate.engineeringmanagement.planning.service.PartService;
import com.digigate.engineeringmanagement.procurementmanagement.constant.*;
import com.digigate.engineeringmanagement.procurementmanagement.dto.projection.*;
import com.digigate.engineeringmanagement.procurementmanagement.dto.request.*;
import com.digigate.engineeringmanagement.procurementmanagement.dto.response.*;
import com.digigate.engineeringmanagement.procurementmanagement.entity.*;
import com.digigate.engineeringmanagement.procurementmanagement.repository.ComparativeStatementRepository;
import com.digigate.engineeringmanagement.procurementmanagement.util.CsPartUtilService;
import com.digigate.engineeringmanagement.procurementmanagement.util.VendorQuotationUtil;
import com.digigate.engineeringmanagement.status.service.DemandStatusService;
import com.digigate.engineeringmanagement.storemanagement.constant.RemarkType;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.ApprovalStatus;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.PartRemark;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.UsernameProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.ApprovalStatusDto;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand.ApprovalStatusViewModel;
import com.digigate.engineeringmanagement.storemanagement.service.StoreVoucherTrackingService;
import com.digigate.engineeringmanagement.storemanagement.service.storeconfiguration.PartRemarkService;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.ApprovalStatusService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.*;
import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.WORKFLOW_ACTION_ORDER.INITIAL_ORDER;
import static com.digigate.engineeringmanagement.common.constant.ApprovalStatusType.*;

@Service
public class ComparativeStatementService extends AbstractSearchService<ComparativeStatement, CsDto, CsSearchDto> {
    private final VendorQuotationInvoiceDetailService vqDetailService;
    private final ComparativeStatementRepository comparativeStatementRepository;
    private final WorkFlowActionService workFlowActionService;
    private final Helper helper;
    private final WorkFlowUtil workFlowUtil;
    private final QuoteRequestService quoteRequestService;
    private final VendorQuotationService vendorQuotationService;
    private final VendorQuotationUtil vendorQuotationUtil;
    private final UserService userService;
    private final StoreVoucherTrackingService storeVoucherTrackingService;
    private final CsAuditDisposalService csAuditDisposalService;
    private final PartService partService;
    private final ApprovalStatusService approvalStatusService;
    private final CsDetailService csDetailService;
    private final CsPartUtilService csPartUtilService;
    private final PartRemarkService partRemarkService;
    private final ApprovalEmployeeService approvalEmployeeService;
    private final VendorQuotationInvoiceDetailService vendorQuotationInvoiceDetailService;
    private final DemandStatusService demandStatusService;


    public ComparativeStatementService(VendorQuotationInvoiceDetailService vqDetailService,
                                       ComparativeStatementRepository comparativeStatementRepository,
                                       WorkFlowActionService workFlowActionService,
                                       Helper helper,
                                       WorkFlowUtil workFlowUtil,
                                       QuoteRequestService quoteRequestService,
                                       VendorQuotationService vendorQuotationService,
                                       VendorQuotationUtil vendorQuotationUtil,
                                       UserService userService,
                                       StoreVoucherTrackingService storeVoucherTrackingService,
                                       CsAuditDisposalService csAuditDisposalService,
                                       PartService partService,
                                       ApprovalStatusService approvalStatusService,
                                       CsDetailService csDetailService, CsPartUtilService csPartUtilService,
                                       PartRemarkService partRemarkService,
                                       ApprovalEmployeeService approvalEmployeeService,
                                       VendorQuotationInvoiceDetailService vendorQuotationInvoiceDetailService,
                                       DemandStatusService demandStatusService) {

        super(comparativeStatementRepository);
        this.vqDetailService = vqDetailService;
        this.comparativeStatementRepository = comparativeStatementRepository;
        this.workFlowActionService = workFlowActionService;
        this.helper = helper;
        this.workFlowUtil = workFlowUtil;
        this.quoteRequestService = quoteRequestService;
        this.vendorQuotationService = vendorQuotationService;
        this.vendorQuotationUtil = vendorQuotationUtil;
        this.userService = userService;
        this.storeVoucherTrackingService = storeVoucherTrackingService;
        this.csAuditDisposalService = csAuditDisposalService;
        this.partService = partService;
        this.approvalStatusService = approvalStatusService;
        this.csDetailService = csDetailService;
        this.csPartUtilService = csPartUtilService;
        this.partRemarkService = partRemarkService;
        this.approvalEmployeeService = approvalEmployeeService;
        this.vendorQuotationInvoiceDetailService = vendorQuotationInvoiceDetailService;
        this.demandStatusService = demandStatusService;
    }

    /** getting CS history for part invoice */
    public List<CsViewModel> getCsHistory(Long partInvoiceId) {
        ExistingCsProjection existingCsProjection = comparativeStatementRepository.getComparativeStatement(partInvoiceId);
        return preparingCsHistory(existingCsProjection, new ArrayList<>());
    }

    public Page<ComparativeStatement> findAll(Specification<ComparativeStatement> comparativeStatementSpecification, Pageable pageable) {
        return comparativeStatementRepository.findAll(comparativeStatementSpecification, pageable);
    }

    public Specification<ComparativeStatement> specification(CsSearchDto dto) {
        return buildSpecification(dto);
    }

    public CsResponseDto getExistingCs(Long id, RfqType rfqType) {
        ComparativeStatement comparativeStatement = comparativeStatementRepository.findByIdAndRfqType(id, rfqType);
        Set<CsDetail> csDetailSet = comparativeStatement.getCsDetailSet();
        Set<CsPartDetail> csPartDetailSet = comparativeStatement.getCsPartDetailSet();
        CsGenerateDto csGenerateDto = populateToCsGenerateDto(comparativeStatement, csDetailSet, csPartDetailSet, rfqType);
        /** CS No */
        csGenerateDto.setCsNo(comparativeStatement.getComparativeStatementNo());
         prepareApprovalRemarks(csGenerateDto,comparativeStatement);
        return populate2dFormat(csGenerateDto, comparativeStatement.getOrderType(), comparativeStatement.getRemarks(), false);
    }

    private void prepareApprovalRemarks(CsGenerateDto csGenerateDto, ComparativeStatement comparativeStatement) {
        List<WorkFlowActionProjection> approvedActionsForUser = approvalEmployeeService
                .findApprovedActionsForUser(helper.getSubModuleItemId(), Helper.getAuthUserId());

        WorkFlowDto workFlowDto = workFlowUtil.prepareResponseData(Set.of(comparativeStatement.getId()), approvedActionsForUser, COMPARATIVE_STATEMENT);
        WorkFlowDto auditWorkFlowDto = workFlowUtil.prepareResponseData(Set.of(comparativeStatement.getId()), approvedActionsForUser, COMPARATIVE_STATEMENT_AUDIT);
        WorkFlowDto finalWorkFlowDto = workFlowUtil.prepareResponseData(Set.of(comparativeStatement.getId()), approvedActionsForUser, COMPARATIVE_STATEMENT_FINAL);

        List<ApprovalStatus> approvalStatuses = workFlowDto.getStatusMap().getOrDefault(comparativeStatement.getId(), new ArrayList<>());
        Map<Long, ApprovalStatus> workFlowActionMapCsInitial = approvalStatuses.stream().collect(Collectors.toMap(ApprovalStatus::getWorkFlowActionId, Function.identity(), (a, b) -> b));

        List<ApprovalStatus> auditApprovalStatuses = auditWorkFlowDto.getStatusMap().getOrDefault(comparativeStatement.getId(), new ArrayList<>());
        Map<Long, ApprovalStatus> workFlowActionMapCsAudit = auditApprovalStatuses.stream().collect(Collectors.toMap(ApprovalStatus::getWorkFlowActionId, Function.identity(), (a, b) -> b));

        List<ApprovalStatus> finalApprovalStatuses = finalWorkFlowDto.getStatusMap().getOrDefault(comparativeStatement.getId(), new ArrayList<>());
        Map<Long, ApprovalStatus> workFlowActionMapCsFinal = finalApprovalStatuses.stream().collect(Collectors.toMap(ApprovalStatus::getWorkFlowActionId, Function.identity(), (a, b) -> b));

        List<PartRemark> partRemarkListCsInitial = partRemarkService.findByParentIdAndRemarkType(Set.of(comparativeStatement.getId()), RemarkType.CS_INITIAL_APPROVAL_REMARK); //CS initial approval  remarks
        List<PartRemark> partRemarkListCsAudit = partRemarkService.findByParentIdAndRemarkType(Set.of(comparativeStatement.getId()), RemarkType.CS_AUDIT_APPROVAL_REMARK); //CS Audit approval  remarks
        List<PartRemark> partRemarkListCsFinal = partRemarkService.findByParentIdAndRemarkType(Set.of(comparativeStatement.getId()), RemarkType.CS_FINAL_APPROVAL_REMARK); //CS final approval  remarks


        if (CollectionUtils.isNotEmpty(partRemarkListCsInitial)) {
            csGenerateDto.setApprovalRemarksResponseDtoList(prepareApprovalRemarksResponse(partRemarkListCsInitial, workFlowActionMapCsInitial, workFlowDto.getNamesFromApprovalStatuses()));
        }
        if (CollectionUtils.isNotEmpty(partRemarkListCsAudit)) {
            csGenerateDto.setApprovalRemarksResponseDtoListAudit(prepareApprovalRemarksResponse(partRemarkListCsAudit, workFlowActionMapCsAudit, auditWorkFlowDto.getNamesFromApprovalStatuses()));
        }
        if (CollectionUtils.isNotEmpty(partRemarkListCsFinal)) {
            csGenerateDto.setApprovalRemarksResponseDtoListFinal(prepareApprovalRemarksResponse(partRemarkListCsFinal, workFlowActionMapCsFinal, finalWorkFlowDto.getNamesFromApprovalStatuses()));
        }
    }

    public CsResponseDto generateCs(CsGenerateDto csGenerateDto, OrderType type) {
        return populate2dFormat(csGenerateDto, type, EMPTY_STRING, true);
    }

    @Transactional
    @Override
    public ComparativeStatement create(CsDto csDto) {
        List<WorkFlowAction> sortedWorkflowActions = workFlowActionService.getSortedWorkflowActions(Sort.Direction.ASC);
        WorkFlowAction workFlowAction = workFlowActionService.getByIndex(INITIAL_ORDER, sortedWorkflowActions);
        Long subModuleItemId = helper.getSubModuleItemId();
        workFlowUtil.validateWorkflow(subModuleItemId, Collections.singletonList(workFlowAction.getId()));

        ComparativeStatement comparativeStatement = convertToEntity(csDto);
        comparativeStatement.setWorkFlowAction(workFlowActionService.getByIndex(INITIAL_ORDER + INT_ONE, sortedWorkflowActions));
        comparativeStatement.setSubmoduleItemId(subModuleItemId);
        ComparativeStatement entity = super.saveItem(comparativeStatement);
        approvalStatusService.create(ApprovalStatusDto.of(comparativeStatement.getId(), COMPARATIVE_STATEMENT, workFlowAction));
        saveOrUpdateDemandStatus(entity);
        return entity;
    }

    public void saveOrUpdateDemandStatus(ComparativeStatement comparativeStatement) {

        if (comparativeStatement.getRfqType().equals(RfqType.PROCUREMENT)) {
            List<VendorQuotation> vendorQuotationList = comparativeStatement.getCsDetailSet().stream().map(CsDetail:: getVendorQuotation).collect(Collectors.toList());
            Set<Long> vendorQuotationIds = vendorQuotationList.stream().map(VendorQuotation::getId).collect(Collectors.toSet());
            List<VendorQuotationInvoiceDetail> vendorQuotationInvoiceDetailList = vendorQuotationInvoiceDetailService.findDetailsByVendorQuotationInvoiceIdIn(vendorQuotationIds);
            if (comparativeStatement.getRfqType().equals(RfqType.PROCUREMENT)) {
                for (VendorQuotationInvoiceDetail vendorQuotationInvoiceDetail : vendorQuotationInvoiceDetailList) {
                    demandStatusService.createWithVQDetailsId(
                            getPartIdForProcurement(vendorQuotationInvoiceDetail),
                            comparativeStatement.getQuoteRequest().getId(),
                            getStoreDemandId(vendorQuotationInvoiceDetail),
                            comparativeStatement.getId(),
                            vendorQuotationInvoiceDetail.getId(),
                            vendorQuotationInvoiceDetail.getPartQuantity(),
                            comparativeStatement.getWorkFlowAction().getId(),
                            VoucherType.CS,
                            RfqType.PROCUREMENT.name()
                    );
                }
            }
        }
        if (comparativeStatement.getRfqType().equals(RfqType.LOGISTIC)) {
            List<VendorQuotation> vendorQuotationList = comparativeStatement.getCsDetailSet().stream().map(CsDetail:: getVendorQuotation).collect(Collectors.toList());
            Set<Long> vendorQuotationIds = vendorQuotationList.stream().map(VendorQuotation::getId).collect(Collectors.toSet());
            List<VendorQuotationInvoiceDetail> vendorQuotationInvoiceDetailList = vendorQuotationInvoiceDetailService.findDetailsByVendorQuotationInvoiceIdIn(vendorQuotationIds);
            for (VendorQuotationInvoiceDetail vendorQuotationInvoiceDetail : vendorQuotationInvoiceDetailList) {
                demandStatusService.createWithVQDetailsId(
                        getPartIdForLogistic(vendorQuotationInvoiceDetail),
                        comparativeStatement.getQuoteRequest().getId(),
                        vendorQuotationInvoiceDetail.getPoItem().getIqItem().getRequisitionItem().getDemandItem().getStoreDemand().getId(),
                        comparativeStatement.getId(),
                        vendorQuotationInvoiceDetail.getId(),
                        vendorQuotationInvoiceDetail.getPartQuantity(),
                        comparativeStatement.getWorkFlowAction().getId(),
                        VoucherType.CS,
                        RfqType.LOGISTIC.name()
                );
            }
        }
    }

    private Long getPartIdForProcurement(VendorQuotationInvoiceDetail vendorQuotationInvoiceDetail) {
        Long partId = OVERLOAD;
        if (Objects.nonNull(vendorQuotationInvoiceDetail.getAlternatePart())) {
            partId = vendorQuotationInvoiceDetail.getAlternatePart().getId();                                                              // (vendorQuotationInvoiceDetail.getAlternatePart())?
        } else {
            if (Objects.nonNull(vendorQuotationInvoiceDetail.getRequisitionItem())) {
                partId = vendorQuotationInvoiceDetail.getRequisitionItem().getDemandItem().getPart().getId();
            }
        }
        return partId;
    }

    private Long getStoreDemandId(VendorQuotationInvoiceDetail vendorQuotationInvoiceDetail) {
        Long storeDemandId = OVERLOAD;
        if (Objects.nonNull(vendorQuotationInvoiceDetail.getRequisitionItem())) {
            storeDemandId = vendorQuotationInvoiceDetail.getRequisitionItem().getDemandItem().getStoreDemand().getId();
        }
        return storeDemandId;
    }

    private Long getPartIdForLogistic(VendorQuotationInvoiceDetail vendorQuotationInvoiceDetail) {
        Long partId = vendorQuotationInvoiceDetail.getPoItem().getIqItem().getRequisitionItem().getDemandItem().getPart().getId();
        if (Objects.nonNull(vendorQuotationInvoiceDetail.getAlternatePart())) {
            partId = vendorQuotationInvoiceDetail.getPoItem().getIqItem().getAlternatePart().getId();
        }
        return partId;
    }

    @Override
    protected CsMainResponseDto convertToResponseDto(ComparativeStatement comparativeStatement) {
        return getAllResponseDto(Collections.singletonList(comparativeStatement), new ArrayList<>()).get(FIRST_INDEX);
    }

    @Override
    protected ComparativeStatement convertToEntity(CsDto csDto) {
        QuoteRequest quoteRequest = quoteRequestService.findById(csDto.getRfqId());
        return populateToEntity(csDto, new ComparativeStatement(), quoteRequest);
    }

    @Transactional
    @Override
    protected ComparativeStatement updateEntity(CsDto dto, ComparativeStatement entity) {
        QuoteRequest quoteRequest = quoteRequestService.findById(dto.getRfqId());
        return populateToEntity(dto, entity, quoteRequest);
    }

    @Override
    protected Specification<ComparativeStatement> buildSpecification(CsSearchDto searchDto) {
        CustomSpecification<ComparativeStatement> customSpecification = new CustomSpecification<>();
        return Specification.where(customSpecification.equalSpecificationAtRoot(searchDto.getRfqId(), ApplicationConstant.RFQ_ID))
                .and(customSpecification.equalSpecificationAtRoot(searchDto.getQuery(), ApplicationConstant.CS_NO))
                .and(customSpecification.equalSpecificationAtRoot(searchDto.getOrderType(), ApplicationConstant.ORDER_TYPE));
    }

    private CsGenerateDto populateToCsGenerateDto(ComparativeStatement comparativeStatement,
                                                  Set<CsDetail> csDetailSet,
                                                  Set<CsPartDetail> csPartDetailSet,
                                                  RfqType rfqType) {
        Set<Long> quotationIds = csDetailSet.stream().map(CsDetail::getVendorQuotationId).collect(Collectors.toSet());

        List<QuotationNoListDto> quotationNoListDtoList = new ArrayList<>();
        csDetailSet.forEach(csDetail -> {
            QuotationNoListDto quotationNoListDto = new QuotationNoListDto();
            quotationNoListDto.setQuotationId(csDetail.getVendorQuotationId());
            quotationNoListDto.setQuotationNo(csDetail.getVendorQuotation().getQuotationNo());
            quotationNoListDtoList.add(quotationNoListDto);
        });

        return CsGenerateDto.builder()
                .rfqId(comparativeStatement.getQuoteRequestId())
                .createdAt(comparativeStatement.getCreatedAt())
                .csNo(comparativeStatement.getComparativeStatementNo())
                .rejectedDesc(comparativeStatement.getRejectedDesc())
                .isRejected(comparativeStatement.getIsRejected())
                .quotationIdList(quotationIds)
                .quotationNoListDtoList(quotationNoListDtoList)
                .csPartDetailSet(csPartDetailSet)
                .csDetailSet(csDetailSet)
                .rfqType(rfqType).build();
    }

    private ComparativeStatement populateToEntity(CsDto csDto,
                                                  ComparativeStatement comparativeStatement,
                                                  QuoteRequest quoteRequest) {
        if (StringUtils.isEmpty(comparativeStatement.getComparativeStatementNo())) {
            quoteRequest = quoteRequestService.findById(csDto.getRfqId());
            comparativeStatement.setComparativeStatementNo(storeVoucherTrackingService.generateUniqueVoucherNo(csDto.getRfqId(),
                    VoucherType.CS, quoteRequest.getRfqNo()));
        }
        comparativeStatement.setQuoteRequest(quoteRequest);
        comparativeStatement.setWorkflowType(CsWorkflowType.CS_INITIAL);
        comparativeStatement.setRfqType(csDto.getRfqType());
        comparativeStatement.setOrderType(csDto.getOrderType());
        comparativeStatement.setRemarks(csDto.getRemarks());
        comparativeStatement.setSubmittedBy(User.withId(Helper.getAuthUserId()));
        if (Objects.nonNull(csDto.getExistingCsId())) {
            comparativeStatement.setExistingCs(findById(csDto.getExistingCsId()));
        }
        comparativeStatement.setCsDetailSet(populateToCSDetail(csDto.getQuotationIdList(), comparativeStatement));
        comparativeStatement.setCsPartDetailSet(populateToCSPartDetail(csDto.getCsPartDetailDtoSet(), comparativeStatement));
        return comparativeStatement;
    }

    private Set<CsDetail> populateToCSDetail(Set<Long> vqIdList, ComparativeStatement comparativeStatement) {

        return vendorQuotationService.getAllByDomainIdIn(vqIdList, true).stream().map(vendorQuotation -> {
            CsDetail csDetail = new CsDetail();
            csDetail.setVendorQuotation(vendorQuotation);
            comparativeStatement.addCsDetail(csDetail);
            return csDetail;
        }).collect(Collectors.toSet());
    }

    private Set<CsPartDetail> populateToCSPartDetail(Set<CsPartDetailDto> csPartDetailDtoSet,
                                                     ComparativeStatement comparativeStatement) {
        Set<Long> iqItemIdSet = csPartDetailDtoSet.stream().map(CsPartDetailDto::getItemId).collect(Collectors.toSet());
        Map<Long, VendorQuotationInvoiceDetail> detailMap = vendorQuotationInvoiceDetailService.getAllByDomainIdIn(iqItemIdSet, true)
                .stream().collect(Collectors.toMap(VendorQuotationInvoiceDetail::getId, Function.identity()));

        return csPartDetailDtoSet.stream().map(csPartDetailDto -> {
            CsPartDetail csPartDetail = new CsPartDetail();
            csPartDetail.setId(csPartDetailDto.getId());
            csPartDetail.setIqItem(detailMap.get(csPartDetailDto.getItemId()));
            csPartDetail.setMoqRemark(csPartDetailDto.getMoqRemark());
            comparativeStatement.addCsPartDetail(csPartDetail);
            return csPartDetail;
        }).collect(Collectors.toSet());
    }

    private CsResponseDto populate2dFormat(CsGenerateDto csGenerateDto, OrderType type, String remarks, boolean isGenerate) {
        QuoteRequestProjection quoteRequestProjection = quoteRequestService.findQuoteRequestById(csGenerateDto.getRfqId()).get();

        Map<Long, CsPartDetail> itemAndCsPartDetailIdMap = new HashMap<>();
        if (Objects.nonNull(csGenerateDto.getCsPartDetailSet())) {
            csGenerateDto.getCsPartDetailSet().stream().filter(Objects::nonNull).forEach(
                    detail -> itemAndCsPartDetailIdMap.put(detail.getIqItemId(), detail));
        }
        List<IqItemProjection> csIqItemProjectionList;
        if (quoteRequestProjection.getRfqType().equals(RfqType.PROCUREMENT)) {
            /** FLOW = QUOTATION -> QUOTATION DETAILS */
            csIqItemProjectionList = vendorQuotationInvoiceDetailService.findDetailsByVendorQuotationIdIn(csGenerateDto.getQuotationIdList(), VendorRequestType.QUOTATION);
        } else {
            /** FLOW = QUOTATION -> QUOTATION DETAILS -> PART ORDER ITEM -> QUOTATION DETAILS */
            csIqItemProjectionList = vendorQuotationInvoiceDetailService.findDetailsByVendorQuotationIdInForLogistic(csGenerateDto.getQuotationIdList(), VendorRequestType.QUOTATION);
        }

        /** part id list from requisition item */
        Set<Long> partIdList = csIqItemProjectionList.stream().map(IqItemProjection::getPartId).collect(Collectors.toSet());
        /** part id from alternate part */
        partIdList.addAll(csIqItemProjectionList.stream().map(IqItemProjection::getAltPartId).collect(Collectors.toSet()));

        Map<Long, Set<Part>> alternateMap = partService.getAllByDomainIdIn(partIdList, true).stream()
                .collect(Collectors.toMap(Part::getId, Part::getAlternatePartSet));

        Map<Long, Boolean> markIqItem = new HashMap<>();
        List<CsItemPartResponseDto> iqItemPartResList = csIqItemProjectionList.stream().filter(item -> isNotDuplicateItem(item, markIqItem))
                .map(itemProjection -> convertToItemPartRes(itemProjection, itemAndCsPartDetailIdMap, alternateMap)).collect(Collectors.toList());

        List<CsQuotationProjection> csQuotationProjectionList = vendorQuotationService.findVendorQuotationByIdIn(csGenerateDto.getQuotationIdList());

        Map<Long, VendorProjection> vendorProjectionMap = vendorQuotationUtil.getVendorMap(csQuotationProjectionList);

        Map<Long, Long> vqAndCsdIdMap = csGenerateDto.getCsDetailSet().stream().collect(Collectors.toMap(CsDetail::getVendorQuotationId, CsDetail::getId));

        List<CsVendorResponseDto> csVendorRes = csQuotationProjectionList.stream().map(quotationProjection ->
                populateToCsQuotation(quotationProjection, vendorProjectionMap, vqAndCsdIdMap, type)).collect(Collectors.toList());

        return CsResponseDto.of(
                csGenerateDto.getRfqId(),
                csGenerateDto.getCsNo(),
                csGenerateDto.getCreatedAt(),
                csGenerateDto.getIsRejected(),
                csGenerateDto.getRejectedDesc(),
                isGenerate ? null : csGenerateDto.getApprovalRemarksResponseDtoList(),
                isGenerate ? null : csGenerateDto.getApprovalRemarksResponseDtoListAudit(),
                isGenerate ? null : csGenerateDto.getApprovalRemarksResponseDtoListFinal(),
                csGenerateDto.getQuotationIdList(),
                csGenerateDto.getQuotationNoListDtoList(),
                iqItemPartResList, csVendorRes, remarks);
    }

    private boolean isNotDuplicateItem(IqItemProjection item, Map<Long, Boolean> markIqItem) {
        Long partId = csPartUtilService.getPartId(item);
        if (BooleanUtils.isNotTrue(markIqItem.get(partId))) {
            markIqItem.put(partId, Boolean.TRUE);
            return true;
        }
        return false;
    }

    private CsItemPartResponseDto convertToItemPartRes(IqItemProjection iqItemProjection,
                                                       Map<Long, CsPartDetail> itemIdAndCsPartDetailMap,
                                                       Map<Long, Set<Part>> alternateMap) {

        if (Objects.isNull(iqItemProjection)) {
            return new CsItemPartResponseDto();
        }

        CsPartDetail csPartDetail = itemIdAndCsPartDetailMap.getOrDefault(iqItemProjection.getId(), new CsPartDetail());

        /** Need to optimize by cs part detail id */
        List<CsAuditDisposalResponseDto> csAuditDisposalResponseDtoList = csAuditDisposalService.findByItemPartId(csPartDetail.getId());

        return CsItemPartResponseDto.builder()
                .id(csPartDetail.getId())
                .moqRemark(csPartDetail.getMoqRemark())
                .qty(iqItemProjection.getReqQuantity())
                /** Here quotation detail is behaving as item*/
                .itemId(iqItemProjection.getId())
                .priority(iqItemProjection.getPriorityType())
                .partId(csPartUtilService.getPartId(iqItemProjection))
                .partNo(csPartUtilService.getPartNo(iqItemProjection))
                .partDescription(csPartUtilService.getPartDescription(iqItemProjection))
                .uomId(csPartUtilService.getUomId(iqItemProjection, OVERLOAD))
                .uomCode(csPartUtilService.getUomCode(iqItemProjection, OVERLOAD))
                .alternate(csPartUtilService.getAlternatePart(iqItemProjection, alternateMap))
                .comments(csAuditDisposalResponseDtoList)
                .build();
    }

    private CsVendorResponseDto populateToCsQuotation(CsQuotationProjection csq,
                                                      Map<Long, VendorProjection> vendorProjectionMap,
                                                      Map<Long, Long> vqAndCsdIdMap,
                                                      OrderType type) {
        if (Objects.isNull(csq)) {
            return new CsVendorResponseDto();
        }

        VendorProjection vendorProjection = vendorProjectionMap.get(csq.getQuoteRequestVendorVendorId());
        List<VqDetailProjection> vqDetailProjections = vqDetailService.findByVendorQuotationId(csq.getId()).stream()
                .filter(vqd -> orderTypeFilter(vqd, type)).collect(Collectors.toList());

        return CsVendorResponseDto.builder()
                .id(csq.getId())
                .csDetailId(vqAndCsdIdMap.get(csq.getId()))
                .vendorId(csq.getQuoteRequestVendorVendorId())
                .vendorType(Objects.nonNull(vendorProjection) ? vendorProjection.getVendorType() : null)
                .vendorName(Objects.nonNull(vendorProjection) ? vendorProjection.getName() : null)
                .validTill(Objects.nonNull(vendorProjection) ? vendorProjection.getValidTill() : null)
                .vendorWorkFlowName(Objects.nonNull(vendorProjection) ? vendorProjection.getWorkFlowActionName() : null)
                .csqDetailResponseDtoList(populateToQuotationDetail(vqDetailProjections))
                .build();
    }

    private boolean orderTypeFilter(VqDetailProjection vqd, OrderType type) {
        if (OrderType.PURCHASE == type) {
            return vqd.getExchangeType() == ExchangeType.PURCHASE;
        } else if (OrderType.EXCHANGE == type) {
            return vqd.getExchangeType() == ExchangeType.EXCHANGE_WITH_COST ||
                    vqd.getExchangeType() == ExchangeType.FLAT_RATE_EXCHANGE_WITH_BER_LIMIT ||
                    vqd.getExchangeType() == ExchangeType.FLAT_RATE_EXCHANGE_WITH_NO_BILL_BACK;
        } else if (OrderType.REPAIR == type) {
            return vqd.getExchangeType() == ExchangeType.REPAIR;
        } else if (OrderType.LOAN == type) {
            return vqd.getExchangeType() == ExchangeType.LOAN;
        } else {
            return false;
        }
    }

    private List<CsqDetailResponseDto> populateToQuotationDetail(List<VqDetailProjection> vqdList) {
        return vqdList.stream().map(vqDetailProjection -> {
            if (Objects.isNull(vqDetailProjection)) {
                return new CsqDetailResponseDto();
            }

            return CsqDetailResponseDto.builder()
                    .detailId(vqDetailProjection.getId())
                    .unitPrice(vqDetailProjection.getUnitPrice())
                    .leadTime(vqDetailProjection.getLeadTime())
                    .incoterms(vqDetailProjection.getIncoterms())
                    .mov(vqDetailProjection.getMov())
                    .mlv(vqDetailProjection.getMlv())
                    .moq(vqDetailProjection.getMoq())
                    .discount(vqDetailProjection.getDiscount())
                    .exchangeType(vqDetailProjection.getExchangeType())
                    .exchangeFee(vqDetailProjection.getExchangeFee())
                    .repairCost(vqDetailProjection.getRepairCost())
                    .berLimit(vqDetailProjection.getBerLimit())
                    .condition(vqDetailProjection.getCondition())
                    .vendorUomCode(vqDetailProjection.getUnitMeasurementCode())
                    .vendorPartQuantity(vqDetailProjection.getPartQuantity())
                    .currencyCode(vqDetailProjection.getCurrencyCode())
                    .partId(csPartUtilService.getPartId(vqDetailProjection, OVERLOAD))
                    .build();
        }).collect(Collectors.toList());
    }

    public List<CsMainResponseDto> getAllResponseDto(List<ComparativeStatement> comparativeStatementList,
                                                     List<WorkFlowActionProjection> approvedActionsForUser) {

        Set<Long> rfqIdSet = comparativeStatementList.stream().map(ComparativeStatement::getQuoteRequestId).collect(Collectors.toSet());

        Map<Long, QuoteRequestProjection> quoteRequestProjectionMap = quoteRequestService.findQuoteRequestByIdIn(rfqIdSet)
                .stream().collect(Collectors.toMap(QuoteRequestProjection::getId, Function.identity()));

        Set<Long> userIdSet = comparativeStatementList.stream().map(ComparativeStatement::getSubmittedId).collect(Collectors.toSet());

        Map<Long, UsernameProjection> usernameProjectionMap = userService.findUsernameByIdList(userIdSet).stream()
                .collect(Collectors.toMap(UsernameProjection::getId, Function.identity()));

        Set<Long> csIdSet = comparativeStatementList.stream().map(ComparativeStatement::getId).collect(Collectors.toSet());

        WorkFlowDto workFlowDto = workFlowUtil.prepareResponseData(csIdSet, approvedActionsForUser, COMPARATIVE_STATEMENT);
        WorkFlowDto auditWorkFlowDto = workFlowUtil.prepareResponseData(csIdSet, approvedActionsForUser, COMPARATIVE_STATEMENT_AUDIT);
        WorkFlowDto finalWorkFlowDto = workFlowUtil.prepareResponseData(csIdSet, approvedActionsForUser, COMPARATIVE_STATEMENT_FINAL);

        Map<Long, List<PartRemark>> partRemarkListCSInitial = partRemarkService.findByParentIdAndRemarkType(csIdSet, RemarkType.CS_INITIAL_APPROVAL_REMARK).stream()
                .collect(Collectors.groupingBy(PartRemark::getParentId)); //CS initial approval  remarks
        Map<Long, List<PartRemark>> partRemarkListCSAudit = partRemarkService.findByParentIdAndRemarkType(csIdSet, RemarkType.CS_AUDIT_APPROVAL_REMARK).stream()
                .collect(Collectors.groupingBy(PartRemark::getParentId)); //CS Audit approval  remarks
        Map<Long, List<PartRemark>> partRemarkListCSFinal = partRemarkService.findByParentIdAndRemarkType(csIdSet, RemarkType.CS_FINAL_APPROVAL_REMARK).stream()
                .collect(Collectors.groupingBy(PartRemark::getParentId)); //CS Final approval  remarks

        //Child CSDetail
        List<CsDetail> csDetails = csDetailService.findByCsIdIn(csIdSet);
        Set<Long> quoteIdSet = csDetails.stream().map(CsDetail::getVendorQuotationId).collect(Collectors.toSet());


        Map<Long, VqProjection> vqProjectionMap = vendorQuotationService.findVQByIdIn(quoteIdSet).stream()
                .collect(Collectors.toMap(VqProjection::getId, Function.identity()));

        //Child CSPartDetail
        Set<Long> iqItemIdList = comparativeStatementList.stream().flatMap(cs -> cs.getCsPartDetailSet().stream()
                .map(CsPartDetail::getIqItemId)).collect(Collectors.toSet());

        Map<Long, IqItemProjection> iqItemProjectionMap = vendorQuotationInvoiceDetailService.findDetailsByIdIn(iqItemIdList, VendorRequestType.QUOTATION)
                .stream().collect(Collectors.toMap(IqItemProjection::getId, Function.identity()));


        return comparativeStatementList.stream().map(comparativeStatement -> convertToViewModel(
                comparativeStatement,
                quoteRequestProjectionMap.get(comparativeStatement.getQuoteRequestId()),
                usernameProjectionMap.get(comparativeStatement.getSubmittedId()),
                workFlowDto,
                auditWorkFlowDto,
                finalWorkFlowDto,
                partRemarkListCSInitial.get(comparativeStatement.getId()),
                partRemarkListCSAudit.get(comparativeStatement.getId()),
                partRemarkListCSFinal.get(comparativeStatement.getId()),
                vqProjectionMap,
                iqItemProjectionMap)).collect(Collectors.toList());
    }

    private CsMainResponseDto convertToViewModel(ComparativeStatement comparativeStatement,
                                                 QuoteRequestProjection quoteRequestProjection,
                                                 UsernameProjection usernameProjection,
                                                 WorkFlowDto workFlowDto, WorkFlowDto auditWorkFlowDto, WorkFlowDto finalWorkFlowDto,
                                                 List<PartRemark> partRemarkListCsInitial, List<PartRemark> partRemarkListCsAudit,
                                                 List<PartRemark> partRemarkListCsFinal, Map<Long, VqProjection> vqProjectionMap,
                                                 Map<Long, IqItemProjection> iqItemProjectionMap) {

        List<ApprovalStatus> approvalStatuses = workFlowDto.getStatusMap().getOrDefault(comparativeStatement.getId(), new ArrayList<>());
        Map<Long, ApprovalStatus> workFlowActionMapCsInitial = approvalStatuses.stream().collect(Collectors.toMap(ApprovalStatus::getWorkFlowActionId, Function.identity(), (a, b) -> b));
        List<ApprovalStatus> auditApprovalStatuses = auditWorkFlowDto.getStatusMap().getOrDefault(comparativeStatement.getId(), new ArrayList<>());
        Map<Long, ApprovalStatus> workFlowActionMapCsAudit = auditApprovalStatuses.stream().collect(Collectors.toMap(ApprovalStatus::getWorkFlowActionId, Function.identity(), (a, b) -> b));
        List<ApprovalStatus> finalApprovalStatuses = finalWorkFlowDto.getStatusMap().getOrDefault(comparativeStatement.getId(), new ArrayList<>());
        Map<Long, ApprovalStatus> workFlowActionMapCsFinal = finalApprovalStatuses.stream().collect(Collectors.toMap(ApprovalStatus::getWorkFlowActionId, Function.identity(), (a, b) -> b));
        WorkFlowAction workFlowAction = workFlowDto.getWorkFlowActionMap().get(comparativeStatement.getWorkFlowActionId());

       CsMainResponseDto csMainResponseDto =  CsMainResponseDto.builder()
                .id(comparativeStatement.getId())
                .rfqId(quoteRequestProjection.getId())
                .rfqNo(quoteRequestProjection.getRfqNo())
                .csNo(comparativeStatement.getComparativeStatementNo())
                .submittedId(Objects.nonNull(usernameProjection) ? usernameProjection.getId() : null)
                .submittedByName(Objects.nonNull(usernameProjection) ? usernameProjection.getLogin() : null)
                .isRejected(comparativeStatement.getIsRejected())
                .rejectedDesc(comparativeStatement.getRejectedDesc())
                .workFlowActionId(comparativeStatement.getWorkFlowActionId())
                .workflowName(Objects.nonNull(workFlowAction) ? workFlowAction.getName() : null)
                .workflowOrder(Objects.nonNull(workFlowAction) ? workFlowAction.getOrderNumber() : null)
                .workflowType(comparativeStatement.getWorkflowType())
                .actionEnabled(!Objects.equals(comparativeStatement.getWorkflowType(), CsWorkflowType.CS_FINAL)
                        || workFlowDto.getActionableIds().contains(comparativeStatement.getWorkFlowActionId()))
                .editable(!Objects.equals(comparativeStatement.getWorkflowType(), CsWorkflowType.CS_FINAL)
                        || workFlowDto.getEditableIds().contains(comparativeStatement.getWorkFlowActionId()))
                .approvalStatuses(approvalStatuses.stream().map(approvalStatus ->
                                ApprovalStatusViewModel.from(approvalStatus, workFlowDto.getNamesFromApprovalStatuses()))
                        .collect(Collectors.toMap(ApprovalStatusViewModel::getWorkFlowActionId,
                                Function.identity(), (a, b) -> b)))
                .auditApprovalStatuses(auditApprovalStatuses.stream().map(approvalStatus ->
                                ApprovalStatusViewModel.from(approvalStatus, auditWorkFlowDto.getNamesFromApprovalStatuses()))
                        .collect(Collectors.toMap(ApprovalStatusViewModel::getWorkFlowActionId,
                                Function.identity(), (a, b) -> b)))
                .finalApprovalStatuses(finalApprovalStatuses.stream().map(approvalStatus ->
                                ApprovalStatusViewModel.from(approvalStatus, finalWorkFlowDto.getNamesFromApprovalStatuses()))
                        .collect(Collectors.toMap(ApprovalStatusViewModel::getWorkFlowActionId,
                                Function.identity(), (a, b) -> b)))
                .csDetailResponseDtoList(comparativeStatement.getCsDetailSet().stream().map(csDetail -> convertToCsDetailResponseDto(
                        csDetail, vqProjectionMap.get(csDetail.getVendorQuotationId()))).collect(Collectors.toList()))
                .csPartDetailResponseDtoList(comparativeStatement.getCsPartDetailSet().stream().map(csPartDetail ->
                        convertToCspDetailResponseDto(csPartDetail, iqItemProjectionMap.get(csPartDetail.getIqItemId()))).collect(Collectors.toList()))
                .build();

        if (CollectionUtils.isNotEmpty(partRemarkListCsInitial)) {
            csMainResponseDto.setApprovalRemarksResponseDtoList(prepareApprovalRemarksResponse(partRemarkListCsInitial, workFlowActionMapCsInitial, workFlowDto.getNamesFromApprovalStatuses()));
        }
        if (CollectionUtils.isNotEmpty(partRemarkListCsAudit)) {
            csMainResponseDto.setApprovalRemarksResponseDtoListAudit(prepareApprovalRemarksResponse(partRemarkListCsAudit, workFlowActionMapCsAudit, auditWorkFlowDto.getNamesFromApprovalStatuses()));
        }
        if (CollectionUtils.isNotEmpty(partRemarkListCsFinal)) {
            csMainResponseDto.setApprovalRemarksResponseDtoListFinal(prepareApprovalRemarksResponse(partRemarkListCsFinal, workFlowActionMapCsFinal, finalWorkFlowDto.getNamesFromApprovalStatuses()));
        }
        return csMainResponseDto;
    }

    private List<ApprovalRemarksResponseDto> prepareApprovalRemarksResponse(List<PartRemark> partRemarkList,
                                                                            Map<Long, ApprovalStatus> workFlowActionMap,
                                                                            Map<Long, Pair<Pair<String, String>, WorkFlowAction>> namesFromApprovalStatuses) {

        return partRemarkList.stream().map(partRemark -> partRemarkService.prepareApprovalRemarkResponse(partRemark,
                workFlowActionMap, namesFromApprovalStatuses)).collect(Collectors.toList());
    }

    private CsPartDetailResponseDto convertToCspDetailResponseDto(CsPartDetail csPartDetail,
                                                                  IqItemProjection iqItemProjection) {
        if (Objects.isNull(iqItemProjection)) {
            return new CsPartDetailResponseDto();
        }

        return CsPartDetailResponseDto.builder()
                .id(csPartDetail.getId())
                .iqItemId(csPartDetail.getIqItemId())
                .itemId(iqItemProjection.getDemandItemId())
                .partId(csPartUtilService.getPartId(iqItemProjection))
                .partNo(csPartUtilService.getPartNo(iqItemProjection))
                .partDescription(csPartUtilService.getPartDescription(iqItemProjection))
                .moqRemark(csPartDetail.getMoqRemark())
                .csAuditDisposalResponseDtoList(csAuditDisposalService.populateResponseDto(csPartDetail.getCsAuditDisposalSet()))
                .build();
    }

    private CsDetailResponseDto convertToCsDetailResponseDto(CsDetail csDetail, VqProjection vqProjection) {
        return CsDetailResponseDto.builder()
                .id(csDetail.getId())
                .quoteId(Objects.nonNull(vqProjection) ? vqProjection.getId() : null)
                .quoteNo(Objects.nonNull(vqProjection) ? vqProjection.getQuotationNo() : null)
                .build();
    }

    public RemarkType getRemarkType(CsWorkflowType workflowType) {
        RemarkType remarkType = null;

            switch (workflowType) {
                case CS_INITIAL:
                    remarkType = RemarkType.CS_INITIAL_APPROVAL_REMARK;
                    break;
                case AUDIT:
                    remarkType = RemarkType.CS_AUDIT_APPROVAL_REMARK;
                    break;
                case CS_FINAL:
                    remarkType = RemarkType.CS_FINAL_APPROVAL_REMARK;
                    break;
            }

        return remarkType;
    }

    private List<CsViewModel> preparingCsHistory(ExistingCsProjection csProjection,
                                                 List<CsViewModel> csViewModelList) {
        if (Objects.isNull(csProjection)) {
            return csViewModelList;
        }

        /** Adding CS and Existing CS */
        csViewModelList.add(CsViewModel.of(
                csProjection.getId(),
                csProjection.getComparativeStatementNo(),
                csProjection.getExistingCsId()));

        return preparingCsHistory(comparativeStatementRepository.findComparativeStatementById(
                csProjection.getExistingCsId()), csViewModelList);
    }
}
