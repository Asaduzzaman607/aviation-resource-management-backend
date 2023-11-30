package com.digigate.engineeringmanagement.procurementmanagement.util;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ApprovalStatusType;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.constant.VoucherType;
import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.common.entity.User;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.common.util.Helper;
import com.digigate.engineeringmanagement.common.util.WorkFlowUtil;
import com.digigate.engineeringmanagement.configurationmanagement.constant.SubModuleItemEnum;
import com.digigate.engineeringmanagement.configurationmanagement.dto.projection.WorkFlowActionProjection;
import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.WorkFlowAction;
import com.digigate.engineeringmanagement.configurationmanagement.service.administration.WorkFlowActionService;
import com.digigate.engineeringmanagement.procurementmanagement.constant.CsWorkflowType;
import com.digigate.engineeringmanagement.procurementmanagement.constant.RfqType;
import com.digigate.engineeringmanagement.procurementmanagement.constant.VendorRequestType;
import com.digigate.engineeringmanagement.procurementmanagement.dto.projection.IqItemProjection;
import com.digigate.engineeringmanagement.procurementmanagement.dto.request.CsAuditDisposalDto;
import com.digigate.engineeringmanagement.procurementmanagement.dto.request.CsRemarksDto;
import com.digigate.engineeringmanagement.procurementmanagement.dto.request.CsSearchDto;
import com.digigate.engineeringmanagement.procurementmanagement.entity.*;
import com.digigate.engineeringmanagement.procurementmanagement.service.ComparativeStatementService;
import com.digigate.engineeringmanagement.procurementmanagement.service.CsAuditDisposalService;
import com.digigate.engineeringmanagement.procurementmanagement.service.VendorQuotationInvoiceDetailService;
import com.digigate.engineeringmanagement.procurementmanagement.service.VendorQuotationService;
import com.digigate.engineeringmanagement.status.constant.WorkFlowType;
import com.digigate.engineeringmanagement.status.service.DemandStatusService;
import com.digigate.engineeringmanagement.storemanagement.constant.RemarkType;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.ApprovalRequestDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.ApprovalStatusDto;
import com.digigate.engineeringmanagement.storemanagement.service.storeconfiguration.PartRemarkService;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.ApprovalStatusService;
import com.digigate.engineeringmanagement.storemanagement.util.SortChanger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.*;
import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.WORKFLOW_ACTION_ORDER.INITIAL_ORDER;
import static com.digigate.engineeringmanagement.common.constant.ApprovalStatusType.*;
import static com.digigate.engineeringmanagement.procurementmanagement.constant.CsWorkflowType.CS_INITIAL;
import static com.digigate.engineeringmanagement.storemanagement.constant.RemarkType.*;
import static java.lang.Boolean.TRUE;

@Component
public class CsUtilService {

    private final WorkFlowUtil workFlowUtil;
    private final ComparativeStatementService comparativeStatementService;
    private final WorkFlowActionService workFlowActionService;
    private final ApprovalStatusService approvalStatusService;
    private final Helper helper;
    private final CsAuditDisposalService csAuditDisposalService;
    private final PartRemarkService partRemarkService;

    private final DemandStatusService demandStatusService;

    private final VendorQuotationInvoiceDetailService vendorQuotationInvoiceDetailService;
    private final VendorQuotationService vendorQuotationService;


    public CsUtilService(WorkFlowUtil workFlowUtil,
                         ComparativeStatementService comparativeStatementService,
                         WorkFlowActionService workFlowActionService,
                         ApprovalStatusService approvalStatusService, Helper helper,
                         CsAuditDisposalService csAuditDisposalService, PartRemarkService partRemarkService,
                         DemandStatusService demandStatusService, VendorQuotationInvoiceDetailService vendorQuotationInvoiceDetailService, VendorQuotationService vendorQuotationService) {
        this.workFlowUtil = workFlowUtil;
        this.comparativeStatementService = comparativeStatementService;
        this.workFlowActionService = workFlowActionService;
        this.approvalStatusService = approvalStatusService;
        this.helper = helper;
        this.csAuditDisposalService = csAuditDisposalService;
        this.partRemarkService = partRemarkService;
        this.demandStatusService = demandStatusService;
        this.vendorQuotationInvoiceDetailService = vendorQuotationInvoiceDetailService;
        this.vendorQuotationService = vendorQuotationService;
    }

    public CsAuditDisposal disposal(CsAuditDisposalDto csAuditDisposalDto) {
        return csAuditDisposalService.create(csAuditDisposalDto);
    }

    public void updateActiveStatus(Long id, Boolean isActive, CsWorkflowType workflowType) {
        ComparativeStatement comparativeStatement = comparativeStatementService.findByIdUnfiltered(id);
        workFlowUtil.validateUpdatability(comparativeStatement.getWorkFlowActionId());
        comparativeStatement.setIsActive(isActive);

        if (isActive == TRUE) {
            WorkFlowAction workFlowAction = workFlowActionService.getNavigatedAction(false, comparativeStatement.getWorkFlowAction());
            workFlowUtil.validateWorkflow(helper.getSubModuleItemId(), List.of(comparativeStatement.getWorkFlowActionId(), workFlowAction.getId()));
            partRemarkService.revertPreviousActionRemarks(comparativeStatement.getId(), workFlowAction,
                    comparativeStatementService.getRemarkType(workflowType));

            comparativeStatement.setWorkFlowAction(workFlowUtil.revertAndFindPrevAction(comparativeStatement.getWorkFlowAction(),
                    workflow(workflowType), comparativeStatement.getId()));
            comparativeStatement.setIsRejected(false);
            setDemandStatusIsActiveUpdatedValue(comparativeStatement);
            setDemandStatusRejectedUpdatedValue(comparativeStatement);
        } else {
            workFlowUtil.validateWorkflow(helper.getSubModuleItemId(), Collections.singletonList(comparativeStatement.getWorkFlowActionId()));
            setDemandStatusIsActiveUpdatedValue(comparativeStatement);
        }
        comparativeStatementService.saveItem(comparativeStatement);
    }

    public void decision(Long id, ApprovalRequestDto approvalRequestDto, CsWorkflowType workflowType, RfqType rfqType) {
        ComparativeStatement comparativeStatement = populateValidity(id);
        populateDecision(approvalRequestDto, comparativeStatement, workflowType, rfqType);
        comparativeStatement.setSubmittedBy(User.withId(Helper.getAuthUserId()));
        comparativeStatementService.saveItem(comparativeStatement);
    }

    public PageData search(CsSearchDto dto, Pageable pageable) {
        pageable = SortChanger.descendingSortByCreatedAt(pageable);
        CustomSpecification<ComparativeStatement> customSpecification = new CustomSpecification<>();
        Specification<ComparativeStatement> comparativeStatementSpecification = Specification.where(customSpecification
                        .active(Objects.nonNull(dto.getIsActive()) ? dto.getIsActive() : true, IS_ACTIVE_FIELD))
                .and(comparativeStatementService.specification(dto));

        Page<ComparativeStatement> pagedData;
        List<WorkFlowActionProjection> approvedActionsForUser = new ArrayList<>();
        switch (dto.getType()) {
            case PENDING:
                Set<Long> pendingSearchWorkFlowIds = workFlowUtil.findPendingWorkFlowIds(approvedActionsForUser);
                comparativeStatementSpecification = comparativeStatementSpecification.and(customSpecification.inSpecificationAtRoot(
                                pendingSearchWorkFlowIds, WORKFLOW_ACTION_ID)
                        .and(customSpecification.equalSpecificationAtRoot(dto.getWorkflowType(), CS_WORKFLOW_TYPE))
                        .and(customSpecification.equalSpecificationAtRoot(dto.getRfqType(), RFQ_TYPE))
                        .and(customSpecification.notEqualSpecificationAtRoot(TRUE, IS_REJECTED)));
                break;
            case APPROVED:
                Long approvedId = workFlowActionService
                        .findFinalAction().getId();
                comparativeStatementSpecification = comparativeStatementSpecification.and(
                        customSpecification.equalSpecificationAtRoot(approvedId, ApplicationConstant.WORKFLOW_ACTION_ID)
                                .and(customSpecification.equalSpecificationAtRoot(dto.getRfqType(), RFQ_TYPE)));
                break;
            case REJECTED:
                comparativeStatementSpecification = Specification.where(
                                customSpecification.equalSpecificationAtRoot(true, ApplicationConstant.IS_REJECTED))
                        .and(customSpecification.equalSpecificationAtRoot(dto.getRfqType(), RFQ_TYPE))
                        .and(comparativeStatementService.specification(dto)).and(customSpecification.equalSpecificationAtRoot(dto.getWorkflowType(), CS_WORKFLOW_TYPE));
                break;
            case ALL:
                comparativeStatementSpecification = Specification.where(customSpecification.equalSpecificationAtRoot(dto.getRfqId(), ApplicationConstant.RFQ_ID)
                        .and(customSpecification.equalSpecificationAtRoot(dto.getRfqType(), RFQ_TYPE)));
                break;
            default:
                break;
        }

        pagedData = comparativeStatementService.findAll(comparativeStatementSpecification, pageable);
        return PageData.builder()
                .model(comparativeStatementService.getAllResponseDto(pagedData.getContent(), approvedActionsForUser))
                .totalPages(pagedData.getTotalPages())
                .totalElements(pagedData.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    public void updatePoRelatedCSWorkFLowToInitialStage(ComparativeStatement comparativeStatement) {
        updateWorkflowActionToInitialStage(comparativeStatement);
        comparativeStatementService.saveItem(comparativeStatement);
    }

    private void populateDecision(ApprovalRequestDto approvalRequestDto, ComparativeStatement comparativeStatement, CsWorkflowType workflowType, RfqType rfqType) {
        if (approvalRequestDto.getApprove() == TRUE) {
            WorkFlowAction nextAction = workFlowActionService.getNavigatedAction(true, comparativeStatement.getWorkFlowAction());

            if (rfqType.equals(RfqType.PROCUREMENT)) {

                prepareDecision(approvalRequestDto, comparativeStatement, nextAction, workflowType, SubModuleItemEnum.MATERIAL_MANAGEMENT_AUDIT_PENDING_CS.getSubModuleItemId(), SubModuleItemEnum.MATERIAL_MANAGEMENT_FINAL_PENDING_CS.getSubModuleItemId());

            } else {
                prepareDecision(approvalRequestDto, comparativeStatement, nextAction, workflowType, SubModuleItemEnum.LOGISTIC_AUDIT_PENDING_CS.getSubModuleItemId(), SubModuleItemEnum.LOGISTIC_FINAL_PENDING_CS.getSubModuleItemId());
            }
        } else {
            comparativeStatement.setIsRejected(true);
            if (StringUtils.isEmpty(approvalRequestDto.getRejectedDesc())) {
                throw EngineeringManagementServerException.notFound
                        (ErrorId.REJECTED_DESCRIPTION_CAN_NOT_BE_EMPTY);
            }
            updateWorkflowActionToInitialStage(comparativeStatement);
            comparativeStatement.setRejectedDesc(approvalRequestDto.getRejectedDesc());
            setDemandStatusRejectedUpdatedValue(comparativeStatement);
        }
    }

    private void setDemandStatusIsActiveUpdatedValue(ComparativeStatement comparativeStatement)
    {
        if (comparativeStatement.getRfqType().equals(RfqType.PROCUREMENT)) {
            List<VendorQuotationInvoiceDetail> vendorQuotationInvoiceDetailList = getInvoiceDetailForProcurement(comparativeStatement);

            for (VendorQuotationInvoiceDetail vendorQuotationInvoiceDetail : vendorQuotationInvoiceDetailList) {
                demandStatusService.updateActiveStatusForCS(
                        vendorQuotationInvoiceDetail.getRequisitionItem().getDemandItem().getStoreDemand().getId(),
                        comparativeStatement.getId(),
                        Objects.nonNull(vendorQuotationInvoiceDetail.getAlternatePart())?vendorQuotationInvoiceDetail.getAlternatePart().getId():
                                vendorQuotationInvoiceDetail.getRequisitionItem().getDemandItem().getPart().getId(),
                        VoucherType.CS,
                        vendorQuotationInvoiceDetail.getId(),
                        comparativeStatement.getIsActive(),
                        comparativeStatement.getWorkFlowAction().getId()
                );
            }
        } else {
            List<VendorQuotation> vendorQuotationList = comparativeStatement.getCsDetailSet().stream().map(CsDetail:: getVendorQuotation).collect(Collectors.toList());
            Set<Long> vendorQuotationIds = vendorQuotationList.stream().map(VendorQuotation::getId).collect(Collectors.toSet());
            List<VendorQuotationInvoiceDetail> vendorQuotationInvoiceDetailList = vendorQuotationInvoiceDetailService.findDetailsByVendorQuotationInvoiceIdIn(vendorQuotationIds);
            for (VendorQuotationInvoiceDetail vendorQuotationInvoiceDetail : vendorQuotationInvoiceDetailList) {
                demandStatusService.updateActiveStatusForCS(
                        vendorQuotationInvoiceDetail.getPoItem().getIqItem().getRequisitionItem().getDemandItem().getStoreDemand().getId(),
                        comparativeStatement.getId(),
                        Objects.nonNull(vendorQuotationInvoiceDetail.getPoItem().getIqItem().getAlternatePart())?vendorQuotationInvoiceDetail.getPoItem().getIqItem().getAlternatePart().getId():
                                vendorQuotationInvoiceDetail.getPoItem().getIqItem().getRequisitionItem().getDemandItem().getPart().getId(),
                        VoucherType.CS,
                        vendorQuotationInvoiceDetail.getId(),
                        comparativeStatement.getIsActive(),
                        comparativeStatement.getWorkFlowAction().getId()
                );
            }
        }
    }

    private void setDemandStatusRejectedUpdatedValue(ComparativeStatement comparativeStatement)
    {
        if (comparativeStatement.getRfqType().equals(RfqType.PROCUREMENT)) {
            List<VendorQuotationInvoiceDetail> vendorQuotationInvoiceDetailList = getInvoiceDetailForProcurement(comparativeStatement);
            for (VendorQuotationInvoiceDetail vendorQuotationInvoiceDetail : vendorQuotationInvoiceDetailList) {
                demandStatusService.updateRejectedStatusForCS(
                        vendorQuotationInvoiceDetail.getRequisitionItem().getDemandItem().getStoreDemand().getId(),
                        comparativeStatement.getId(),
                        Objects.nonNull(vendorQuotationInvoiceDetail.getAlternatePart())? vendorQuotationInvoiceDetail.getAlternatePart().getId()
                                :vendorQuotationInvoiceDetail.getRequisitionItem().getDemandItem().getPart().getId(),
                        VoucherType.CS,
                        vendorQuotationInvoiceDetail.getId(),
                        comparativeStatement.getIsRejected(),
                        comparativeStatement.getWorkFlowAction().getId()
                );
            }
        } else {
            List<VendorQuotation> vendorQuotationList = comparativeStatement.getCsDetailSet().stream().map(CsDetail:: getVendorQuotation).collect(Collectors.toList());
            Set<Long> vendorQuotationIds = vendorQuotationList.stream().map(VendorQuotation::getId).collect(Collectors.toSet());
            List<VendorQuotationInvoiceDetail> vendorQuotationInvoiceDetailList = vendorQuotationInvoiceDetailService.findDetailsByVendorQuotationInvoiceIdIn(vendorQuotationIds);
            for (VendorQuotationInvoiceDetail vendorQuotationInvoiceDetail : vendorQuotationInvoiceDetailList) {
                demandStatusService.updateRejectedStatusForCS(
                        vendorQuotationInvoiceDetail.getPoItem().getIqItem().getRequisitionItem().getDemandItem().getStoreDemand().getId(),
                        comparativeStatement.getId(),
                        Objects.nonNull(vendorQuotationInvoiceDetail.getPoItem().getIqItem().getAlternatePart())?vendorQuotationInvoiceDetail.getPoItem().getIqItem().getAlternatePart().getId():
                                vendorQuotationInvoiceDetail.getPoItem().getIqItem().getRequisitionItem().getDemandItem().getPart().getId(),
                        VoucherType.CS,
                        vendorQuotationInvoiceDetail.getId(),
                        comparativeStatement.getIsRejected(),
                        comparativeStatement.getWorkFlowAction().getId()
                );
            }
        }
    }

    private List<VendorQuotationInvoiceDetail> getInvoiceDetailForProcurement(ComparativeStatement comparativeStatement) {
        Long quoteRequestId = comparativeStatement.getQuoteRequest().getId();
        List<VendorQuotation> vendorQuotationList = vendorQuotationService.findByQuoteRequestId(quoteRequestId);
        Set<Long> vendorQuotationIds = vendorQuotationList.stream().map(VendorQuotation::getId).collect(Collectors.toSet());
        return vendorQuotationInvoiceDetailService.findDetailsByVendorQuotationInvoiceIdIn(vendorQuotationIds);
    }

    private List<IqItemProjection> getIqItemProjectionForLogistics(ComparativeStatement comparativeStatement) {
        List<VendorQuotationInvoiceDetail> vendorQuotationInvoiceDetailList = comparativeStatement.getCsPartDetailSet().stream().map(CsPartDetail::getIqItem).collect(Collectors.toList());
        Set<Long> vendorQuotationInvoiceIds = vendorQuotationInvoiceDetailList.stream().map(AbstractDomainBasedEntity::getId).collect(Collectors.toSet());
        return vendorQuotationInvoiceDetailService.findDetailsByIdInForLogistic(vendorQuotationInvoiceIds, VendorRequestType.QUOTATION);
    }


    private void prepareDecision(ApprovalRequestDto approvalRequestDto,ComparativeStatement comparativeStatement, WorkFlowAction nextAction, CsWorkflowType workflowType, Long csAudit, Long csFinal) {
        saveApprovalRemarks(approvalRequestDto, comparativeStatement, workflowType);
        if (nextAction.equals(workFlowActionService.findFinalAction()) && comparativeStatement.getWorkflowType().equals(CsWorkflowType.CS_INITIAL)) {
            approvalStatusService.create(ApprovalStatusDto.of(comparativeStatement.getId(), COMPARATIVE_STATEMENT, comparativeStatement.getWorkFlowAction()));
            prepareNextWorkflowAction(comparativeStatement, csAudit, CsWorkflowType.AUDIT);
            updateDemandStatus(comparativeStatement, comparativeStatement.getWorkFlowAction(), WorkFlowType.AUDIT.name());

        } else if (nextAction.equals(workFlowActionService.findFinalAction()) && comparativeStatement.getWorkflowType().equals(CsWorkflowType.AUDIT)) {
            approvalStatusService.create(ApprovalStatusDto.of(comparativeStatement.getId(), COMPARATIVE_STATEMENT_AUDIT, comparativeStatement.getWorkFlowAction()));
            prepareNextWorkflowAction(comparativeStatement, csFinal, CsWorkflowType.CS_FINAL);
            updateDemandStatus(comparativeStatement, comparativeStatement.getWorkFlowAction(), WorkFlowType.CS_FINAL.name());
        } else {
            approvalStatusService.create(ApprovalStatusDto.of(comparativeStatement.getId(), workflow(workflowType), comparativeStatement.getWorkFlowAction()));
            comparativeStatement.setWorkFlowAction(nextAction);
            comparativeStatement.setIsRejected(false);
            updateDemandStatus(comparativeStatement, comparativeStatement.getWorkFlowAction(), workflowType.name());
        }
    }

    private void updateDemandStatus(ComparativeStatement comparativeStatement, WorkFlowAction nextAction, String workFlowType) {

        if (comparativeStatement.getRfqType().equals(RfqType.PROCUREMENT)) {
            List<VendorQuotationInvoiceDetail> vendorQuotationInvoiceDetailList = getInvoiceDetailForProcurement(comparativeStatement);
            for (VendorQuotationInvoiceDetail vendorQuotationInvoiceDetail : vendorQuotationInvoiceDetailList) {
                demandStatusService.updateWithWftAndVQDetailsId(
                        Objects.nonNull(vendorQuotationInvoiceDetail.getAlternatePart()) ? vendorQuotationInvoiceDetail.getAlternatePart().getId() :
                                vendorQuotationInvoiceDetail.getRequisitionItem().getDemandItem().getPart().getId(),
                        comparativeStatement.getId(),
                        vendorQuotationInvoiceDetail.getId(),
                        nextAction.getId(),
                        comparativeStatement.getIsRejected(),
                        VoucherType.CS,
                        workFlowType
                );
            }
        }

        if (comparativeStatement.getRfqType().equals(RfqType.LOGISTIC)) {
            List<VendorQuotation> vendorQuotationList = comparativeStatement.getCsDetailSet().stream().map(CsDetail::getVendorQuotation).collect(Collectors.toList());
            Set<Long> vendorQuotationIds = vendorQuotationList.stream().map(VendorQuotation::getId).collect(Collectors.toSet());
            List<VendorQuotationInvoiceDetail> vendorQuotationInvoiceDetailList = vendorQuotationInvoiceDetailService.findDetailsByVendorQuotationInvoiceIdIn(vendorQuotationIds);

            for (VendorQuotationInvoiceDetail vendorQuotationInvoiceDetail : vendorQuotationInvoiceDetailList) {
                demandStatusService.updateWithWftAndVQDetailsId(
                        Objects.nonNull(vendorQuotationInvoiceDetail.getPoItem().getIqItem().getAlternatePart()) ? vendorQuotationInvoiceDetail.getPoItem().getIqItem().getAlternatePart().getId() :
                                vendorQuotationInvoiceDetail.getPoItem().getIqItem().getRequisitionItem().getDemandItem().getPart().getId(),
                        comparativeStatement.getId(),
                        vendorQuotationInvoiceDetail.getId(),
                        comparativeStatement.getWorkFlowAction().getId(),
                        comparativeStatement.getIsRejected(),
                        VoucherType.CS,
                        workFlowType
                );
            }

    }
}

    private void saveApprovalRemarks (ApprovalRequestDto approvalRequestDto, ComparativeStatement
            comparativeStatement, CsWorkflowType workflowType){
        if (StringUtils.isEmpty(approvalRequestDto.getApprovalDesc())) {
            throw EngineeringManagementServerException.notFound(ErrorId.APPROVAL_REMARK_CAN_NOT_BE_EMPTY);
        }
        partRemarkService.saveApproveRemark(comparativeStatement.getId(), comparativeStatement.getWorkFlowAction().getId(),
                comparativeStatementService.getRemarkType(workflowType), approvalRequestDto.getApprovalDesc());// save approval remarks
    }

    private void prepareNextWorkflowAction (ComparativeStatement comparativeStatement, Long subModuleItemId, CsWorkflowType workflowType){
        List<WorkFlowAction> sortedWorkflowActions = workFlowActionService.getSortedWorkflowActions(Sort.Direction.ASC, subModuleItemId);
        WorkFlowAction workFlowAction = workFlowActionService.getByIndex(INITIAL_ORDER, sortedWorkflowActions);
        comparativeStatement.setWorkFlowAction(workFlowActionService.getByIndex(INITIAL_ORDER + INT_ONE, sortedWorkflowActions));
        comparativeStatement.setWorkflowType(workflowType);
        comparativeStatement.setSubmoduleItemId(subModuleItemId);
        approvalStatusService.create(ApprovalStatusDto.of(comparativeStatement.getId(), workflow(workflowType), workFlowAction));
    }

    private ApprovalStatusType workflow (CsWorkflowType workflowType){
        ApprovalStatusType workFlowType;
        if (workflowType.equals(CsWorkflowType.CS_INITIAL)) {
            workFlowType = COMPARATIVE_STATEMENT;
        } else if (workflowType.equals(CsWorkflowType.AUDIT)) {
            workFlowType = COMPARATIVE_STATEMENT_AUDIT;
        } else {
            workFlowType = COMPARATIVE_STATEMENT_FINAL;
        }
        return workFlowType;
    }

    private ComparativeStatement populateValidity (Long id){
        ComparativeStatement comparativeStatement = comparativeStatementService.findById(id);
        Long subModuleItemId = helper.getSubModuleItemId();
        workFlowUtil.validateUpdatability(comparativeStatement.getWorkFlowAction().getId());
        workFlowUtil.validateWorkflow(subModuleItemId, Collections.singletonList(comparativeStatement.getWorkFlowAction().getId()));

        return comparativeStatement;
    }

    public void updateRemarks (Long id, CsRemarksDto remarksDto){
        ComparativeStatement comparativeStatement = comparativeStatementService.findByIdUnfiltered(id);
        comparativeStatement.setRemarks(remarksDto.getRemarks());
        comparativeStatementService.saveItem(comparativeStatement);
    }

    private void updateWorkflowActionToInitialStage(ComparativeStatement comparativeStatement) {
        comparativeStatement.setWorkflowType(CS_INITIAL);
        comparativeStatement.setSubmoduleItemId(getNextSubModuleItemId(comparativeStatement.getRfqType()));
        List<WorkFlowAction> sortedWorkflowActions = workFlowActionService.getSortedWorkflowActions(Sort.Direction.ASC,
                getInitialSubModuleItemId(comparativeStatement.getRfqType()));

        List<ApprovalStatusType> approvalStatusTypeList = Arrays.asList(COMPARATIVE_STATEMENT, COMPARATIVE_STATEMENT_AUDIT, COMPARATIVE_STATEMENT_FINAL);
        helper.deleteAllByParentIdAndApprovalStatusTypes(comparativeStatement.getId(), approvalStatusTypeList);

        List<RemarkType> remarkTypeList = Arrays.asList(CS_INITIAL_APPROVAL_REMARK, CS_AUDIT_APPROVAL_REMARK, CS_FINAL_APPROVAL_REMARK);
        helper.deleteByParentIdAndRemarkTypeIn(comparativeStatement.getId(), remarkTypeList);

        approvalStatusService.create(ApprovalStatusDto.of(comparativeStatement.getId(), COMPARATIVE_STATEMENT, workFlowActionService.getByIndex(INITIAL_ORDER, sortedWorkflowActions)));
        comparativeStatement.setWorkFlowAction(workFlowActionService.getByIndex(INITIAL_ORDER + INT_ONE, sortedWorkflowActions));
    }

    private Long getInitialSubModuleItemId(RfqType rfqType) {
        return rfqType.equals(RfqType.PROCUREMENT) ? SubModuleItemEnum.MATERIAL_MANAGEMENT_GENERATE_CS.getSubModuleItemId()
                : SubModuleItemEnum.LOGISTIC_GENERATE_CS.getSubModuleItemId();
    }

    private Long getNextSubModuleItemId(RfqType rfqType) {
        return rfqType.equals(RfqType.PROCUREMENT) ? SubModuleItemEnum.MATERIAL_MANAGEMENT_AUDIT_PENDING_CS.getSubModuleItemId()
                : SubModuleItemEnum.LOGISTIC_AUDIT_PENDING_CS.getSubModuleItemId();
    }
}
