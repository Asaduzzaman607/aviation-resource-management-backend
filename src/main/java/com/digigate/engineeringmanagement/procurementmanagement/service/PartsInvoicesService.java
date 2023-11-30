package com.digigate.engineeringmanagement.procurementmanagement.service;

import com.digigate.engineeringmanagement.common.constant.ApprovalStatusType;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.constant.VoucherType;
import com.digigate.engineeringmanagement.common.entity.User;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.payload.request.search.WorkFlowDto;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.util.Helper;
import com.digigate.engineeringmanagement.common.util.WorkFlowUtil;
import com.digigate.engineeringmanagement.configurationmanagement.constant.SubModuleItemEnum;
import com.digigate.engineeringmanagement.configurationmanagement.dto.projection.WorkFlowActionProjection;
import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.WorkFlowAction;
import com.digigate.engineeringmanagement.configurationmanagement.service.administration.ApprovalEmployeeService;
import com.digigate.engineeringmanagement.configurationmanagement.service.administration.WorkFlowActionService;
import com.digigate.engineeringmanagement.procurementmanagement.constant.InputType;
import com.digigate.engineeringmanagement.procurementmanagement.constant.PartsInVoiceWorkFlowType;
import com.digigate.engineeringmanagement.procurementmanagement.constant.RfqType;
import com.digigate.engineeringmanagement.procurementmanagement.constant.VendorRequestType;
import com.digigate.engineeringmanagement.procurementmanagement.dto.request.PISearchDto;
import com.digigate.engineeringmanagement.procurementmanagement.dto.request.PartsInvoicesDto;
import com.digigate.engineeringmanagement.procurementmanagement.dto.response.PartInvoiceItemResponseDto;
import com.digigate.engineeringmanagement.procurementmanagement.dto.response.PartsInvoicesViewModel;
import com.digigate.engineeringmanagement.procurementmanagement.entity.PartOrder;
import com.digigate.engineeringmanagement.procurementmanagement.entity.PartOrderItem;
import com.digigate.engineeringmanagement.procurementmanagement.entity.PartsInvoice;
import com.digigate.engineeringmanagement.procurementmanagement.entity.VendorQuotationInvoiceDetail;
import com.digigate.engineeringmanagement.procurementmanagement.repository.PartsInvoicesRepository;
import com.digigate.engineeringmanagement.status.service.DemandStatusService;
import com.digigate.engineeringmanagement.status.serviceImpl.DemandStatusServiceImpl;
import com.digigate.engineeringmanagement.storemanagement.constant.FeatureName;
import com.digigate.engineeringmanagement.storemanagement.constant.RemarkType;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.ApprovalStatus;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.PartRemark;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.GenericAttachment;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.ApprovalRequestDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.ApprovalStatusDto;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand.ApprovalStatusViewModel;
import com.digigate.engineeringmanagement.storemanagement.service.StoreVoucherTrackingService;
import com.digigate.engineeringmanagement.storemanagement.service.storeconfiguration.PartRemarkService;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.ApprovalStatusService;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.GenericAttachmentService;
import com.digigate.engineeringmanagement.storemanagement.util.SortChanger;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.INT_ONE;
import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.WORKFLOW_ACTION_ORDER.INITIAL_ORDER;
import static com.digigate.engineeringmanagement.common.constant.ApprovalStatusType.*;
import static com.digigate.engineeringmanagement.procurementmanagement.constant.PartsInVoiceWorkFlowType.OWN_DEPARTMENT;
import static com.digigate.engineeringmanagement.storemanagement.constant.RemarkType.*;
import static java.lang.Boolean.TRUE;

@Service
public class PartsInvoicesService extends AbstractSearchService<PartsInvoice, PartsInvoicesDto, PISearchDto> {
    private final PartsInvoicesRepository partsInvoicesRepository;
    private final PartOrderService partOrderService;
    private final PartOrderItemService partOrderItemService;
    private final ApprovalStatusService approvalStatusService;
    private final WorkFlowActionService workFlowActionService;
    private final VendorQuotationInvoiceFeeService vendorQuotationInvoiceFeeService;
    private final ApprovalEmployeeService approvalEmployeeService;
    private final ComparativeStatementService comparativeStatementService;
    private final Helper helper;
    private final VendorQuotationInvoiceDetailService vendorQuotationInvoiceDetailService;
    private final WorkFlowUtil workFlowUtil;
    private final StoreVoucherTrackingService voucherTrackingService;
    private final GenericAttachmentService genericAttachmentService;
    private final PartRemarkService partRemarkService;
    private final PartsInvoiceItemService partsInvoiceItemService;
    private final DemandStatusService demandStatusService;
    private final DemandStatusServiceImpl demandStatusServiceImpl;

    public PartsInvoicesService(PartsInvoicesRepository partsInvoicesRepository,
                                PartOrderService partOrderService,
                               ApprovalStatusService approvalStatusService,
                                WorkFlowActionService workFlowActionService,
                                VendorQuotationInvoiceFeeService vendorQuotationInvoiceFeeService,
                                ApprovalEmployeeService approvalEmployeeService,
                                ComparativeStatementService comparativeStatementService,
                                Helper helper,
                                VendorQuotationInvoiceDetailService vendorQuotationInvoiceDetailService,
                                WorkFlowUtil workFlowUtil,
                                StoreVoucherTrackingService voucherTrackingService,
                                PartRemarkService partRemarkService,
                                PartsInvoiceItemService partsInvoiceItemService,
                                GenericAttachmentService genericAttachmentService,
                                DemandStatusService demandStatusService,
                                DemandStatusServiceImpl demandStatusServiceImpl,
                                PartOrderItemService partOrderItemService) {
        super(partsInvoicesRepository);
        this.partsInvoicesRepository = partsInvoicesRepository;
        this.partOrderService = partOrderService;
        this.approvalStatusService = approvalStatusService;
        this.workFlowActionService = workFlowActionService;
        this.vendorQuotationInvoiceFeeService = vendorQuotationInvoiceFeeService;
        this.approvalEmployeeService = approvalEmployeeService;
        this.comparativeStatementService = comparativeStatementService;
        this.helper = helper;
        this.vendorQuotationInvoiceDetailService = vendorQuotationInvoiceDetailService;
        this.workFlowUtil = workFlowUtil;
        this.voucherTrackingService = voucherTrackingService;
        this.genericAttachmentService = genericAttachmentService;
        this.partRemarkService = partRemarkService;
        this.partsInvoiceItemService = partsInvoiceItemService;
        this.demandStatusService = demandStatusService;
        this.demandStatusServiceImpl = demandStatusServiceImpl;
        this.partOrderItemService = partOrderItemService;
    }

    @Transactional
    @Override
    public PartsInvoice create(PartsInvoicesDto partsInvoicesDto) {
        List<WorkFlowAction> sortedWorkflowAction = workFlowActionService.getSortedWorkflowActions(Sort.Direction.ASC);
        WorkFlowAction workFlowAction = workFlowActionService.getByIndex(INITIAL_ORDER, sortedWorkflowAction);
        Long subModuleItemId = helper.getSubModuleItemId();
        workFlowUtil.validateWorkflow(subModuleItemId, Collections.singletonList(workFlowAction.getId()));

        PartsInvoice partsInvoice = convertToEntity(partsInvoicesDto);
        partsInvoice.setWorkFlowAction(workFlowActionService.getByIndex(INITIAL_ORDER + INT_ONE, sortedWorkflowAction));
        partsInvoice.setSubmoduleItemId(subModuleItemId);
        PartsInvoice entity = super.saveItem(partsInvoice);
        if (Objects.nonNull(partsInvoicesDto.getAttachment())) {
            genericAttachmentService.saveAllAttachments(partsInvoicesDto.getAttachment(), featureType(partsInvoicesDto.getRfqType()), partsInvoice.getId());

        }
        if (CollectionUtils.isNotEmpty(partsInvoicesDto.getVendorQuotationDetails())) {
            vendorQuotationInvoiceDetailService.createOrUpdateDetails(partsInvoicesDto.getVendorQuotationDetails(), entity.getId(),
                    partsInvoicesDto.getPartOrderId(), VendorRequestType.INVOICE, null, partsInvoicesDto.getRfqType(), InputType.CS);
        }
        if (CollectionUtils.isNotEmpty(partsInvoicesDto.getVendorQuotationFees())) {
            vendorQuotationInvoiceFeeService.createOrUpdateFees(partsInvoicesDto.getVendorQuotationFees(), entity.getId(),
                    VendorRequestType.INVOICE, null);
        }

        approvalStatusService.create(ApprovalStatusDto.of(partsInvoice.getId(), workflow(partsInvoicesDto.getPartsInVoiceWorkFlowType()), workFlowAction));
            partsInvoiceItemService.saveAll(entity.getPartOrder().getPartOrderItemList(), partsInvoice);


        if (partsInvoicesDto.getRfqType().equals(RfqType.PROCUREMENT)) {

            List<VendorQuotationInvoiceDetail> vendorQuotationInvoiceDetailList =
                    partsInvoice.getPartOrder().getPartOrderItemList().stream().
                            map(PartOrderItem::getIqItem).collect(Collectors.toList());

            for (VendorQuotationInvoiceDetail vendorQuotationInvoiceDetail : vendorQuotationInvoiceDetailList) {
                demandStatusService.create(
                        Objects.nonNull(vendorQuotationInvoiceDetail.getAlternatePart())?
                                vendorQuotationInvoiceDetail.getAlternatePart().getId():vendorQuotationInvoiceDetail.getRequisitionItem().getDemandItem().getPart().getId(),
                        partsInvoice.getPartOrder().getId(),
                        vendorQuotationInvoiceDetail.getRequisitionItem().getDemandItem().getStoreDemand().getId(),
                        partsInvoice.getId(),
                        vendorQuotationInvoiceDetail.getPartQuantity(),
                        partsInvoice.getWorkFlowAction().getId(),
                        VoucherType.PI,
                        partsInvoice.getIsActive(),
                        RfqType.PROCUREMENT.name()
                );
            }
        } else {
            List<PartOrderItem> partOrderItemList = partOrderItemService.findByPartOrderId(partsInvoicesDto.getPartOrderId());

            List<VendorQuotationInvoiceDetail> vendorQuotationInvoiceDetailList = partOrderItemList.stream().map(PartOrderItem::getIqItem).collect(Collectors.toList());

            for (VendorQuotationInvoiceDetail vendorQuotationInvoiceDetail : vendorQuotationInvoiceDetailList) {
                demandStatusService.create(
                        // vendorQuotationInvoiceDetail.getPoItem().getIqItem().getRequisitionItem().getDemandItem().getPart().getId(),
                        Objects.nonNull(vendorQuotationInvoiceDetail.getPoItem().getIqItem().getAlternatePart())?vendorQuotationInvoiceDetail.getPoItem().getIqItem().getAlternatePart().getId():
                                vendorQuotationInvoiceDetail.getPoItem().getIqItem().getRequisitionItem().getDemandItem().getPart().getId(),
                        partsInvoice.getPartOrder().getId(),
                        vendorQuotationInvoiceDetail.getPoItem().getIqItem().getRequisitionItem().getDemandItem().getStoreDemandId(),
                        partsInvoice.getId(),
                        vendorQuotationInvoiceDetail.getPartQuantity(),
                        partsInvoice.getWorkFlowAction().getId(),
                        VoucherType.PI,
                        partsInvoice.getIsActive(),
                        RfqType.LOGISTIC.name()
                );
            }
        }
        return partsInvoice;
    }

    private FeatureName featureType(RfqType rfqType) {
        return rfqType.equals(RfqType.PROCUREMENT) ? FeatureName.PART_INVOICE : FeatureName.LOGISTIC_PART_INVOICE;
    }


    public void updateActiveStatus(Long id, Boolean isActive, PartsInVoiceWorkFlowType partsInVoiceWorkFlowType) {
        PartsInvoice partsInvoice = findByIdUnfiltered(id);
        workFlowUtil.validateUpdatability(partsInvoice.getWorkFlowActionId());
        partsInvoice.setIsActive(isActive);

        if (isActive == TRUE) {
            WorkFlowAction workFlowAction = workFlowActionService.getNavigatedAction(false, partsInvoice.getWorkFlowAction());
            workFlowUtil.validateWorkflow(helper.getSubModuleItemId(), List.of(partsInvoice.getWorkFlowActionId(), workFlowAction.getId()));
            partRemarkService.revertPreviousActionRemarks(partsInvoice.getId(), workFlowAction, getRemarkType(partsInVoiceWorkFlowType));
            partsInvoice.setWorkFlowAction(workFlowUtil.revertAndFindPrevAction(partsInvoice.getWorkFlowAction(),
                    workflow(partsInVoiceWorkFlowType), partsInvoice.getId()));
            partsInvoice.setIsRejected(false);
            setDemandStatusIsActiveUpdatedValue(partsInvoice);
            setDemandStatusRejectedUpdatedValue(partsInvoice);
        } else {
            workFlowUtil.validateWorkflow(helper.getSubModuleItemId(), Collections.singletonList(partsInvoice.getWorkFlowActionId()));
            setDemandStatusIsActiveUpdatedValue(partsInvoice);
        }

        super.saveItem(partsInvoice);
    }

    private RemarkType getRemarkType(PartsInVoiceWorkFlowType partsInVoiceWorkFlowType) {
        RemarkType remarkType = null;

        switch (partsInVoiceWorkFlowType) {
            case OWN_DEPARTMENT:
                remarkType = PI_OWN_DEPARTMENT_APPROVAL_REMARK;
                break;
            case AUDIT:
                remarkType = RemarkType.PI_AUDIT_APPROVAL_REMARK;
                break;
            case FINANCE:
                remarkType = RemarkType.PI_FINANCE_APPROVAL_REMARK;
                break;
        }
        return remarkType;
    }

    private ApprovalStatusType workflow(PartsInVoiceWorkFlowType workflowType) {
        ApprovalStatusType workFlowType;
        if (workflowType.equals(OWN_DEPARTMENT)) {
            workFlowType = PARTS_INVOICE;
        } else if (workflowType.equals(PartsInVoiceWorkFlowType.AUDIT)) {
            workFlowType = PARTS_INVOICE_AUDIT;
        } else {
            workFlowType = PARTS_INVOICE_FINANCE;
        }
        return workFlowType;
    }

    @Transactional
    @Override
    public PartsInvoice update(PartsInvoicesDto partsInvoicesDto, Long id) {
        PartsInvoice partsInvoices = findByIdUnfiltered(id);
        Long subModuleItemId = helper.getSubModuleItemId();

        workFlowUtil.validateUpdatability(partsInvoices.getWorkFlowActionId());

        WorkFlowAction currentAction = partsInvoices.getWorkFlowAction();
        workFlowUtil.validateWorkflow(subModuleItemId, Arrays.asList(currentAction.getId(),
                workFlowActionService.getNavigatedAction(false, currentAction).getId()));

        PartsInvoice updatedPartsInvoice = updateEntity(partsInvoicesDto, partsInvoices);

        genericAttachmentService.updateByRecordId(featureType(partsInvoicesDto.getRfqType()), updatedPartsInvoice.getId(), partsInvoicesDto.getAttachment());

        if (CollectionUtils.isNotEmpty(partsInvoicesDto.getVendorQuotationDetails())) {
            vendorQuotationInvoiceDetailService.createOrUpdateDetails(partsInvoicesDto.getVendorQuotationDetails(), partsInvoices.getId(),
                    partsInvoicesDto.getPartOrderId(), VendorRequestType.INVOICE, id, partsInvoicesDto.getRfqType(), InputType.CS);
        }

        if (CollectionUtils.isNotEmpty(partsInvoicesDto.getVendorQuotationFees())) {
            vendorQuotationInvoiceFeeService.createOrUpdateFees(partsInvoicesDto.getVendorQuotationFees(), partsInvoices.getId(),
                    VendorRequestType.INVOICE, id);
        }
        PartsInvoice partsInvoice = super.saveItem(updatedPartsInvoice);
        boolean isDeleted = true;
        if (partsInvoicesDto.getRfqType().equals(RfqType.PROCUREMENT)) {

            List<VendorQuotationInvoiceDetail> vendorQuotationInvoiceDetailList =
                    partsInvoice.getPartOrder().getPartOrderItemList().stream().
                            map(PartOrderItem::getIqItem).collect(Collectors.toList());

            for (VendorQuotationInvoiceDetail vendorQuotationInvoiceDetail : vendorQuotationInvoiceDetailList) {
                if (isDeleted) {
                    demandStatusServiceImpl.deleteAllDemandStatus(
                            vendorQuotationInvoiceDetail.getRequisitionItem().getDemandItem().getStoreDemand().getId(),
                            partsInvoice.getId(),
                            VoucherType.PI);
                    isDeleted = false;
                }
                demandStatusService.entityUpdateWithRejectStatus(
                        Objects.nonNull(vendorQuotationInvoiceDetail.getAlternatePart())?vendorQuotationInvoiceDetail.getAlternatePart().getId():
                                vendorQuotationInvoiceDetail.getRequisitionItem().getDemandItem().getPart().getId(),
                        partsInvoice.getPartOrder().getId(),
                        vendorQuotationInvoiceDetail.getRequisitionItem().getDemandItem().getStoreDemand().getId(),
                        partsInvoice.getId(),
                        vendorQuotationInvoiceDetail.getPartQuantity(),
                        partsInvoice.getWorkFlowAction().getId(),
                        VoucherType.PI,
                        partsInvoice.getIsActive(),
                        RfqType.PROCUREMENT.name(),
                        partsInvoice.getIsRejected()
                );
            }
        } else {
            List<PartOrderItem> partOrderItemList = partOrderItemService.findByPartOrderId(partsInvoicesDto.getPartOrderId());

            List<VendorQuotationInvoiceDetail> vendorQuotationInvoiceDetailList = partOrderItemList.stream().map(PartOrderItem::getIqItem).collect(Collectors.toList());

            for (VendorQuotationInvoiceDetail vendorQuotationInvoiceDetail : vendorQuotationInvoiceDetailList) {

                if (isDeleted) {
                    demandStatusServiceImpl.deleteAllDemandStatus(
                            vendorQuotationInvoiceDetail.getPoItem().getIqItem().getRequisitionItem().getDemandItem().getStoreDemand().getId(),
                            partsInvoice.getId(),
                            VoucherType.PI);
                    isDeleted = false;
                }
                demandStatusService.entityUpdateWithRejectStatus(
                        Objects.nonNull(vendorQuotationInvoiceDetail.getPoItem().getIqItem().getAlternatePart())?vendorQuotationInvoiceDetail.getPoItem().getIqItem().getAlternatePart().getId():
                                vendorQuotationInvoiceDetail.getPoItem().getIqItem().getRequisitionItem().getDemandItem().getPart().getId(),
                        partsInvoice.getPartOrder().getId(),
                        vendorQuotationInvoiceDetail.getPoItem().getIqItem().getRequisitionItem().getDemandItem().getStoreDemand().getId(),
                        partsInvoice.getId(),
                        vendorQuotationInvoiceDetail.getPartQuantity(),
                        partsInvoice.getWorkFlowAction().getId(),
                        VoucherType.PI,
                        partsInvoice.getIsActive(),
                        RfqType.LOGISTIC.name(),
                        partsInvoice.getIsRejected()
                );
            }
        }

        return partsInvoice;
    }

    @Transactional
    public void makeDecision(Long id, ApprovalRequestDto approvalRequestDto, RfqType rfqType, PartsInVoiceWorkFlowType workFlowType) {
        PartsInvoice partsInvoices = findByIdUnfiltered(id);
        Long subModuleItemId = helper.getSubModuleItemId();

        workFlowUtil.validateUpdatability(partsInvoices.getWorkFlowActionId());
        workFlowUtil.validateWorkflow(subModuleItemId, Collections.singletonList(partsInvoices.getWorkFlowActionId()));

        if (approvalRequestDto.getApprove() == TRUE) {
            WorkFlowAction nextAction = workFlowActionService.getNavigatedAction(true, partsInvoices.getWorkFlowAction());
            if (rfqType.equals(RfqType.PROCUREMENT)) {
                prepareDecision(approvalRequestDto, partsInvoices, nextAction, workFlowType, SubModuleItemEnum.MATERIAL_MANAGEMENT_PENDING_PARTS_INVOICE_AUDIT.getSubModuleItemId(), SubModuleItemEnum.MATERIAL_MANAGEMENT_PENDING_PARTS_INVOICE_FINANCE.getSubModuleItemId());
            } else {
                prepareDecision(approvalRequestDto, partsInvoices, nextAction, workFlowType, SubModuleItemEnum.LOGISTIC_PENDING_PARTS_INVOICE_AUDIT.getSubModuleItemId(), SubModuleItemEnum.LOGISTIC_PENDING_PARTS_INVOICE_FINANCE.getSubModuleItemId());
            }
        } else {
            partsInvoices.setIsRejected(true);
            if (org.springframework.util.StringUtils.isEmpty(approvalRequestDto.getRejectedDesc())) {
                throw EngineeringManagementServerException.notFound
                        (ErrorId.REJECTED_DESCRIPTION_CAN_NOT_BE_EMPTY);
            }
            updateWorkflowActionToInitialStage(partsInvoices);
            partsInvoices.setRejectedDesc(approvalRequestDto.getRejectedDesc());
            setDemandStatusRejectedUpdatedValue(partsInvoices);
        }
        super.saveItem(partsInvoices);
    }

    private Long getInitialSubModuleItemId(RfqType rfqType) {
        return rfqType.equals(RfqType.PROCUREMENT) ? SubModuleItemEnum.MATERIAL_MANAGEMENT_PARTS_INVOICE.getSubModuleItemId()
                : SubModuleItemEnum.LOGISTIC_PARTS_INVOICE.getSubModuleItemId();
    }

    private Long getNextSubModuleItemId(RfqType rfqType) {
        return rfqType.equals(RfqType.PROCUREMENT) ? SubModuleItemEnum.MATERIAL_MANAGEMENT_PENDING_PARTS_INVOICE_AUDIT.getSubModuleItemId()
                : SubModuleItemEnum.LOGISTIC_PENDING_PARTS_INVOICE_AUDIT.getSubModuleItemId();
    }

    private void prepareDecision(ApprovalRequestDto approvalRequestDto, PartsInvoice partsInvoices, WorkFlowAction nextAction, PartsInVoiceWorkFlowType workFlowType, Long audit, Long finance) {
        saveApprovalRemarks(approvalRequestDto, partsInvoices, workFlowType);

        if (nextAction.equals(workFlowActionService.findFinalAction()) && partsInvoices.getWorkFlowType().equals(OWN_DEPARTMENT)) {
            approvalStatusService.create(ApprovalStatusDto.of(partsInvoices.getId(), PARTS_INVOICE, partsInvoices.getWorkFlowAction()));
            prepareNextWorkflowAction(partsInvoices, audit, PartsInVoiceWorkFlowType.AUDIT);
        } else if (nextAction.equals(workFlowActionService.findFinalAction()) && partsInvoices.getWorkFlowType().equals(PartsInVoiceWorkFlowType.AUDIT)) {
            approvalStatusService.create(ApprovalStatusDto.of(partsInvoices.getId(), PARTS_INVOICE_AUDIT, partsInvoices.getWorkFlowAction()));
            prepareNextWorkflowAction(partsInvoices, finance, PartsInVoiceWorkFlowType.FINANCE);
        } else {
            approvalStatusService.create(ApprovalStatusDto.of(partsInvoices.getId(), workflow(workFlowType), partsInvoices.getWorkFlowAction()));
            partsInvoices.setWorkFlowAction(nextAction);

            if (partsInvoices.getRfqType().equals(RfqType.PROCUREMENT)) {

                List<VendorQuotationInvoiceDetail> vendorQuotationInvoiceDetailList =
                        partsInvoices.getPartOrder().getPartOrderItemList().stream().
                                map(PartOrderItem::getIqItem).collect(Collectors.toList());

                vendorQuotationInvoiceDetailList.forEach(vendorQuotationInvoiceDetail -> {
                    demandStatusService.updateWithWft(
                            Objects.nonNull(vendorQuotationInvoiceDetail.getAlternatePart())?vendorQuotationInvoiceDetail.getAlternatePart().getId():
                                    vendorQuotationInvoiceDetail.getRequisitionItem().getDemandItem().getPart().getId(),
                            partsInvoices.getId(),
                            partsInvoices.getWorkFlowAction().getId(),
                            partsInvoices.getIsRejected(),
                            VoucherType.PI,
                            RfqType.PROCUREMENT.name());
                });

            } else {
                List<PartOrderItem> partOrderItemList = partOrderItemService.findByPartOrderId(partsInvoices.getPartOrderId());

                List<VendorQuotationInvoiceDetail> vendorQuotationInvoiceDetailList = partOrderItemList.stream().map(PartOrderItem::getIqItem).collect(Collectors.toList());
                vendorQuotationInvoiceDetailList.forEach(vendorQuotationInvoiceDetail -> {
                    demandStatusService.updateWithWft(
                            Objects.nonNull(vendorQuotationInvoiceDetail.getPoItem().getIqItem().getAlternatePart())?vendorQuotationInvoiceDetail.getPoItem().getIqItem().getAlternatePart().getId():
                                    vendorQuotationInvoiceDetail.getPoItem().getIqItem().getRequisitionItem().getDemandItem().getPart().getId(),
                            partsInvoices.getId(),
                            partsInvoices.getWorkFlowAction().getId(),
                            partsInvoices.getIsRejected(),
                            VoucherType.PI,
                            RfqType.LOGISTIC.name());
                });
            }
        }
    }

    private void saveApprovalRemarks(ApprovalRequestDto approvalRequestDto, PartsInvoice partsInvoices, PartsInVoiceWorkFlowType workFlowType) {
        if (StringUtils.isEmpty(approvalRequestDto.getApprovalDesc())) {
            throw EngineeringManagementServerException.notFound(ErrorId.APPROVAL_REMARK_CAN_NOT_BE_EMPTY);
        }
        partRemarkService.saveApproveRemark(partsInvoices.getId(), partsInvoices.getWorkFlowAction().getId(),
                getRemarkType(workFlowType), approvalRequestDto.getApprovalDesc());// save approval remarks
    }

    private void prepareNextWorkflowAction(PartsInvoice partsInvoice, Long subModuleItemId, PartsInVoiceWorkFlowType workFlowType) {
        List<WorkFlowAction> sortedWorkflowActions = workFlowActionService.getSortedWorkflowActions(Sort.Direction.ASC, subModuleItemId);
        WorkFlowAction workFlowAction = workFlowActionService.getByIndex(INITIAL_ORDER, sortedWorkflowActions);
        partsInvoice.setWorkFlowAction(workFlowActionService.getByIndex(INITIAL_ORDER + INT_ONE, sortedWorkflowActions));
        partsInvoice.setWorkFlowType(workFlowType);
        partsInvoice.setSubmoduleItemId(subModuleItemId);
        partsInvoice.setIsRejected(false);

        if (partsInvoice.getRfqType().equals(RfqType.PROCUREMENT)) {

            List<VendorQuotationInvoiceDetail> vendorQuotationInvoiceDetailList =
                    partsInvoice.getPartOrder().getPartOrderItemList().stream().
                            map(PartOrderItem::getIqItem).collect(Collectors.toList());

            vendorQuotationInvoiceDetailList.forEach(vendorQuotationInvoiceDetail -> {
                demandStatusService.updateWithWft(
                        Objects.nonNull(vendorQuotationInvoiceDetail.getAlternatePart())?vendorQuotationInvoiceDetail.getAlternatePart().getId():
                                vendorQuotationInvoiceDetail.getRequisitionItem().getDemandItem().getPart().getId(),
                        partsInvoice.getId(),
                        partsInvoice.getWorkFlowAction().getId(),
                        partsInvoice.getIsRejected(),
                        VoucherType.PI,
                        workFlowType.name());
            });
        } else {
            List<PartOrderItem> partOrderItemList = partOrderItemService.findByPartOrderId(partsInvoice.getPartOrderId());

            List<VendorQuotationInvoiceDetail> vendorQuotationInvoiceDetailList = partOrderItemList.stream().map(PartOrderItem::getIqItem).collect(Collectors.toList());

            vendorQuotationInvoiceDetailList.forEach(vendorQuotationInvoiceDetail -> {
                demandStatusService.updateWithWft(
                        Objects.nonNull(vendorQuotationInvoiceDetail.getPoItem().getIqItem().getAlternatePart())?vendorQuotationInvoiceDetail.getPoItem().getIqItem().getAlternatePart().getId():
                                vendorQuotationInvoiceDetail.getPoItem().getIqItem().getRequisitionItem().getDemandItem().getPart().getId(),
                        partsInvoice.getId(),
                        partsInvoice.getWorkFlowAction().getId(),
                        partsInvoice.getIsRejected(),
                        VoucherType.PI,
                        workFlowType.name());
            });
        }
        approvalStatusService.create(ApprovalStatusDto.of(partsInvoice.getId(), workflow(workFlowType), workFlowAction));

    }

    @Override
    public PageData search(PISearchDto dto, Pageable pageable) {
        pageable = SortChanger.descendingSortByCreatedAt(pageable);
        Page<PartsInvoice> pageData;
        List<WorkFlowActionProjection> approvedActionForUser = new ArrayList<>();
        switch (dto.getType()) {
            case PENDING:
                Set<Long> pendingSearchWorkFlowIds = workFlowUtil.findPendingWorkFlowIds(approvedActionForUser);
                if (CollectionUtils.isEmpty(pendingSearchWorkFlowIds)) {
                    pageData = Page.empty();
                    break;
                }
                pageData = partsInvoicesRepository
                        .findAllByIsRejectedFalseAndIsActiveAndWorkFlowActionIdInAndInvoiceNoContainsAndRfqTypeAndWorkFlowType(dto.getIsActive(),
                                pendingSearchWorkFlowIds, dto.getQuery(), dto.getRfqType(), dto.getPartsInVoiceWorkFlowType(), pageable);
                break;
            case APPROVED:
                Long approvedId = workFlowActionService.findFinalAction().getId();
                pageData = partsInvoicesRepository.
                        findAllByIsActiveAndWorkFlowActionIdAndInvoiceNoContainsAndRfqType(
                                dto.getIsActive(), approvedId, dto.getQuery(), dto.getRfqType(), pageable);
                break;
            case REJECTED:
                pageData = partsInvoicesRepository
                        .findAllByIsRejectedTrueAndInvoiceNoContainsAndRfqTypeAndWorkFlowType(dto.getQuery(), dto.getRfqType(), dto.getPartsInVoiceWorkFlowType(), pageable);
                break;
            default:
                pageData = partsInvoicesRepository.
                        findAllByIsActiveAndInvoiceNoContainsAndRfqType(dto.getIsActive(), dto.getQuery(), dto.getRfqType(), pageable);
                break;
        }

        return PageData.builder()
                .model(getResponseData(pageData.getContent(), approvedActionForUser, dto.getRfqType()))
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    @Override
    public PartsInvoicesViewModel getSingle(Long id) {
        List<WorkFlowActionProjection> approvedActionsForUser = approvalEmployeeService
                .findApprovedActionsForUser(helper.getSubModuleItemId(), Helper.getAuthUserId());
        PartsInvoice partsInvoice = findByIdUnfiltered(id);
        return getResponseData(Collections.singletonList(partsInvoice),
                approvedActionsForUser, partsInvoice.getRfqType()).stream().findFirst().orElseThrow(() ->
                EngineeringManagementServerException.notFound(ErrorId.DATA_NOT_FOUND));
    }

    @Override
    protected Specification<PartsInvoice> buildSpecification(PISearchDto searchDto) {
        return null;
    }

    @Override
    protected <T> T convertToResponseDto(PartsInvoice partsInvoices) {
        return null;
    }

    @Override
    protected PartsInvoice convertToEntity(PartsInvoicesDto partsInvoicesDto) {
        return populateEntity(partsInvoicesDto, new PartsInvoice());
    }

    @Override
    protected PartsInvoice updateEntity(PartsInvoicesDto dto, PartsInvoice entity) {
        return populateEntity(dto, entity);
    }

    private PartsInvoice populateEntity(PartsInvoicesDto dto, PartsInvoice entity) {
        validate(dto, entity);
        if (StringUtils.isEmpty(entity.getInvoiceNo())) {
            PartOrder partOrder = partOrderService.findById(dto.getPartOrderId());
            entity.setInvoiceNo(voucherTrackingService.generateUniqueVoucherNo(dto.getPartOrderId(),
                    VoucherType.PI, partOrder.getOrderNo()));
        }
        entity.setWorkFlowType(dto.getPartsInVoiceWorkFlowType());
        entity.setSubmittedBy(User.withId(Helper.getAuthUserId()));
        entity.setInvoiceType(dto.getInvoiceType());
        entity.setTac(dto.getTac());
        entity.setVendorFax(dto.getVendorFax());
        entity.setVendorWebsite(dto.getVendorWebsite());
        entity.setVendorFrom(dto.getVendorFrom());
        entity.setFollowUpBy(dto.getFollowUpBy());
        entity.setToFax(dto.getToFax());
        entity.setToTel(dto.getToTel());
        entity.setRemark(dto.getRemark());
        entity.setShipTo(dto.getShipTo());
        entity.setBillTo(dto.getBillTo());
        entity.setUpdateDate(LocalDate.now());
        entity.setPaymentTerms(dto.getPaymentTerms());
        entity.setVendorAddress(dto.getVendorAddress());
        entity.setVendorEmail(dto.getVendorEmail());
        entity.setVendorTelephone(dto.getVendorTelephone());
        entity.setRfqType(dto.getRfqType());
        if (Objects.nonNull(dto.getPartOrderId()) &&
                !dto.getPartOrderId().equals(entity.getPartOrderId())) {
            entity.setPartOrder(partOrderService.findByIdUnfiltered(dto.getPartOrderId()));
        }
        return entity;
    }



    private List<PartsInvoicesViewModel> getResponseData(List<PartsInvoice> partsInvoicesList,
                                                         List<WorkFlowActionProjection> approvedActions,RfqType rfqType) {

        Set<Long> issueIds = partsInvoicesList.stream().map(PartsInvoice::getId).collect(Collectors.toSet());
        Set<Long> collectionOfPartOrderIds = partsInvoicesList.stream()
                .map(PartsInvoice::getPartOrderId).collect(Collectors.toSet());

        Map<Long, PartOrder> partOrderProjectionMap =
                partOrderService.findByPartOrderIdIn(collectionOfPartOrderIds).stream().collect(Collectors
                        .toMap(PartOrder::getId, Function.identity()));

        WorkFlowDto workFlowDto = workFlowUtil.prepareResponseData(issueIds, approvedActions, PARTS_INVOICE);
        WorkFlowDto auditWorkFlowDto = workFlowUtil.prepareResponseData(issueIds, approvedActions, PARTS_INVOICE_AUDIT);
        WorkFlowDto financeWorkFlowDto = workFlowUtil.prepareResponseData(issueIds, approvedActions, PARTS_INVOICE_FINANCE);

        Map<Long, List<PartRemark>> partRemarkListPiOwnDepartment = getApprovalRemarks(issueIds, PI_OWN_DEPARTMENT_APPROVAL_REMARK); //Own Department approval  remarks
        Map<Long, List<PartRemark>> partRemarkListPiAudit = getApprovalRemarks(issueIds, RemarkType.PI_AUDIT_APPROVAL_REMARK); // Audit approval  remarks
        Map<Long, List<PartRemark>> partRemarkListPiFinance = getApprovalRemarks(issueIds, RemarkType.PI_FINANCE_APPROVAL_REMARK); //Finance approval  remarks

        Map<Long, Set<String>> attachmentLinksMap = genericAttachmentService.getAllAttachmentByFeatureNameAndId(featureType(rfqType), issueIds)
                .stream().collect(Collectors.groupingBy(GenericAttachment::getRecordId, Collectors.mapping(GenericAttachment::getLink, Collectors.toSet())));

        Map<Long, List<PartInvoiceItemResponseDto>> piItemResponseMap = partsInvoiceItemService.getAllResponse(issueIds, rfqType).stream().collect(Collectors.groupingBy(PartInvoiceItemResponseDto::getPartInvoiceId));

        return partsInvoicesList.stream().map(partsInvoices -> convertToResponseDto(partsInvoices,
                attachmentLinksMap.get(partsInvoices.getId()), partOrderProjectionMap.get(partsInvoices.getPartOrderId()),
                workFlowDto, auditWorkFlowDto, financeWorkFlowDto, partRemarkListPiOwnDepartment.get(partsInvoices.getId()),
                partRemarkListPiAudit.get(partsInvoices.getId()), partRemarkListPiFinance.get(partsInvoices.getId()),
                piItemResponseMap.getOrDefault(partsInvoices.getId(), new ArrayList<>()))).collect(Collectors.toList());
    }

    private Map<Long, List<PartRemark>> getApprovalRemarks(Set<Long> invoiceIds, RemarkType remarkType) {
        return partRemarkService.findByParentIdAndRemarkType(invoiceIds, remarkType).stream().collect(Collectors.groupingBy(PartRemark::getParentId));
    }

    private PartsInvoicesViewModel convertToResponseDto(PartsInvoice partsInvoices,
                                                        Set<String> attachmentLinks,
                                                        PartOrder partOrder,
                                                        WorkFlowDto workFlowDto, WorkFlowDto auditWorkFlowDto,
                                                        WorkFlowDto financeWorkFlowDto,
                                                        List<PartRemark> partRemarkListPiOwnDepartment,
                                                        List<PartRemark> partRemarkListPiAudit,
                                                        List<PartRemark> partRemarkListPiFinance,
                                                        List<PartInvoiceItemResponseDto> partInvoiceItemResponseDtoList) {

        List<ApprovalStatus> approvalStatuses = workFlowDto.getStatusMap().getOrDefault(partsInvoices.getId(), new ArrayList<>());
        List<ApprovalStatus> auditApprovalStatuses = auditWorkFlowDto.getStatusMap().getOrDefault(partsInvoices.getId(), new ArrayList<>());
        List<ApprovalStatus> financeApprovalStatuses = financeWorkFlowDto.getStatusMap().getOrDefault(partsInvoices.getId(), new ArrayList<>());
        WorkFlowAction workFlowAction = workFlowDto.getWorkFlowActionMap().get(partsInvoices.getWorkFlowActionId());

        Map<Long, ApprovalStatus> workFlowActionMapPiOwn = approvalStatuses.stream().collect(Collectors.toMap(ApprovalStatus::getWorkFlowActionId, Function.identity(), (a, b) -> b));
        Map<Long, ApprovalStatus> workFlowActionMapPiAudit = auditApprovalStatuses.stream().collect(Collectors.toMap(ApprovalStatus::getWorkFlowActionId, Function.identity(), (a, b) -> b));
        Map<Long, ApprovalStatus> workFlowActionMapPiFinance = financeApprovalStatuses.stream().collect(Collectors.toMap(ApprovalStatus::getWorkFlowActionId, Function.identity(), (a, b) -> b));

        PartsInvoicesViewModel viewModel = new PartsInvoicesViewModel();

        viewModel.setId(partsInvoices.getId());
        viewModel.setInvoiceNo(partsInvoices.getInvoiceNo());
        viewModel.setInvoiceType(partsInvoices.getInvoiceType());
        viewModel.setAttachment(attachmentLinks);
        viewModel.setTac(partsInvoices.getTac());
        viewModel.setVendorAddress(partsInvoices.getVendorAddress());
        viewModel.setVendorEmail(partsInvoices.getVendorEmail());
        viewModel.setVendorTelephone(partsInvoices.getVendorTelephone());
        viewModel.setVendorFax(partsInvoices.getVendorFax());
        viewModel.setVendorWebsite(partsInvoices.getVendorWebsite());
        viewModel.setVendorFrom(partsInvoices.getVendorFrom());
        viewModel.setFollowUpBy(partsInvoices.getFollowUpBy());
        viewModel.setToFax(partsInvoices.getToFax());
        viewModel.setToTel(partsInvoices.getToTel());
        viewModel.setRemark(partsInvoices.getRemark());
        viewModel.setBillTo(partsInvoices.getBillTo());
        viewModel.setPaymentTerms(partsInvoices.getPaymentTerms());
        viewModel.setUpdateDate(partsInvoices.getUpdateDate());
        viewModel.setWorkflowName(workFlowAction.getName());
        viewModel.setWorkFlowType(partsInvoices.getWorkFlowType());
        viewModel.setRejectedDesc(partsInvoices.getRejectedDesc());
        viewModel.setIsRejected(partsInvoices.getIsRejected());
        viewModel.setSubmittedById(User.withId(Helper.getAuthUserId()).getId());
        viewModel.setActionEnabled(workFlowDto.getActionableIds().contains(partsInvoices.getWorkFlowActionId()));
        viewModel.setEditable(workFlowDto.getEditableIds().contains(partsInvoices.getWorkFlowActionId()));
        viewModel.setWorkFlowActionId(partsInvoices.getWorkFlowActionId());
        viewModel.setWorkflowOrder(workFlowAction.getOrderNumber());
        viewModel.setVendorQuotationDetails(vendorQuotationInvoiceDetailService.getAllVendorQuotationDetailByType(
                partsInvoices.getId(), VendorRequestType.INVOICE, partsInvoices.getRfqType()));
        viewModel.setVendorQuotationFees(vendorQuotationInvoiceFeeService.getAllVendorQuotationFeeByType(
                partsInvoices.getId(), VendorRequestType.INVOICE));
        if (Objects.nonNull(partOrder)) {
            viewModel.setPartOrderId(partOrder.getId());
            viewModel.setPartOrderNo(partOrder.getOrderNo());
        }
        viewModel.setApprovalStatuses(approvalStatuses.stream().map(approvalStatus -> ApprovalStatusViewModel.from(
                approvalStatus, workFlowDto.getNamesFromApprovalStatuses())).collect(Collectors.toMap(
                ApprovalStatusViewModel::getWorkFlowActionId, Function.identity(), (a, b) -> b)));
        viewModel.setAuditApprovalStatuses(auditApprovalStatuses.stream().map(approvalStatus -> ApprovalStatusViewModel.from(
                approvalStatus, auditWorkFlowDto.getNamesFromApprovalStatuses())).collect(Collectors.toMap(
                ApprovalStatusViewModel::getWorkFlowActionId, Function.identity(), (a, b) -> b)));
        viewModel.setFinanceApprovalStatuses(financeApprovalStatuses.stream().map(approvalStatus -> ApprovalStatusViewModel.from(
                approvalStatus, financeWorkFlowDto.getNamesFromApprovalStatuses())).collect(Collectors.toMap(
                ApprovalStatusViewModel::getWorkFlowActionId, Function.identity(), (a, b) -> b)));

        if (CollectionUtils.isNotEmpty(partRemarkListPiOwnDepartment)) {
            viewModel.setApprovalRemarksResponseDtoList(partRemarkListPiOwnDepartment.stream().map(partRemark ->
                    partRemarkService.prepareApprovalRemarkResponse(partRemark, workFlowActionMapPiOwn, workFlowDto.getNamesFromApprovalStatuses())).collect(Collectors.toList()));
        }
        if (CollectionUtils.isNotEmpty(partRemarkListPiAudit)) {
            viewModel.setApprovalRemarksResponseDtoListAudit(partRemarkListPiAudit.stream().map(partRemark ->
                    partRemarkService.prepareApprovalRemarkResponse(partRemark, workFlowActionMapPiAudit, auditWorkFlowDto.getNamesFromApprovalStatuses())).collect(Collectors.toList()));
        }
        if (CollectionUtils.isNotEmpty(partRemarkListPiFinance)) {
            viewModel.setApprovalRemarksResponseDtoListFinance(partRemarkListPiFinance.stream().map(partRemark ->
                    partRemarkService.prepareApprovalRemarkResponse(partRemark, workFlowActionMapPiFinance, financeWorkFlowDto.getNamesFromApprovalStatuses())).collect(Collectors.toList()));
        }
        viewModel.setPartInvoiceItemDtoList(partInvoiceItemResponseDtoList);

        /** List of CS history */
        viewModel.setCsViewModelList(comparativeStatementService.getCsHistory(partsInvoices.getId()));
        return viewModel;
    }

    public PartsInvoice findByInvoiceNo(String invoiceNo) {
        return partsInvoicesRepository.findByInvoiceNo(invoiceNo).orElseThrow(() ->
                EngineeringManagementServerException.notFound(ErrorId.PART_INVOICES_NOT_FOUND));
    }

    public void partiallyUpdate(PartsInvoicesDto partsInvoicesDto) {
        partsInvoiceItemService.updatePartiallyApproved(partsInvoicesDto.getPartInvoiceItemDtoList());
    }

    private void setDemandStatusIsActiveUpdatedValue(PartsInvoice partsInvoice) {

        if (partsInvoice.getRfqType().equals(RfqType.PROCUREMENT)) {
            List<VendorQuotationInvoiceDetail> vendorQuotationInvoiceDetailList =
                    partsInvoice.getPartOrder().getPartOrderItemList().stream().
                            map(PartOrderItem::getIqItem).collect(Collectors.toList());

            for (VendorQuotationInvoiceDetail vendorQuotationInvoiceDetail : vendorQuotationInvoiceDetailList) {
                demandStatusService.updateActiveStatus(
                        vendorQuotationInvoiceDetail.getRequisitionItem().getDemandItem().getStoreDemand().getId(),
                        partsInvoice.getId(),
                        Objects.nonNull(vendorQuotationInvoiceDetail.getAlternatePart())?vendorQuotationInvoiceDetail.getAlternatePart().getId():
                        vendorQuotationInvoiceDetail.getRequisitionItem().getDemandItem().getPart().getId(),
                        VoucherType.PI,
                        partsInvoice.getIsActive(),
                        partsInvoice.getWorkFlowAction().getId()
                );
            }
        } else {

            List<PartOrderItem> partOrderItemList = partOrderItemService.findByPartOrderId(partsInvoice.getPartOrderId());

            List<VendorQuotationInvoiceDetail> vendorQuotationInvoiceDetailList = partOrderItemList.stream().map(PartOrderItem::getIqItem).collect(Collectors.toList());

            for (VendorQuotationInvoiceDetail vendorQuotationInvoiceDetail : vendorQuotationInvoiceDetailList) {
                demandStatusService.updateActiveStatus(
                        vendorQuotationInvoiceDetail.getPoItem().getIqItem().getRequisitionItem().getDemandItem().getStoreDemand().getId(),
                        partsInvoice.getId(),
                        Objects.nonNull(vendorQuotationInvoiceDetail.getPoItem().getIqItem().getAlternatePart())?vendorQuotationInvoiceDetail.getPoItem().getIqItem().getAlternatePart().getId():
                                vendorQuotationInvoiceDetail.getPoItem().getIqItem().getRequisitionItem().getDemandItem().getPart().getId(),
                        VoucherType.PI,
                        partsInvoice.getIsActive(),
                        partsInvoice.getWorkFlowAction().getId()
                );
            }
        }
    }

    private void setDemandStatusRejectedUpdatedValue(PartsInvoice partsInvoice){
            if (partsInvoice.getRfqType().equals(RfqType.PROCUREMENT)) {

                List<VendorQuotationInvoiceDetail> vendorQuotationInvoiceDetailList =
                        partsInvoice.getPartOrder().getPartOrderItemList().stream().
                                map(PartOrderItem::getIqItem).collect(Collectors.toList());

                for (VendorQuotationInvoiceDetail vendorQuotationInvoiceDetail : vendorQuotationInvoiceDetailList) {
                    demandStatusService.updateRejectedStatus(
                            vendorQuotationInvoiceDetail.getRequisitionItem().getDemandItem().getStoreDemand().getId(),
                            partsInvoice.getId(),
                            Objects.nonNull(vendorQuotationInvoiceDetail.getAlternatePart())?vendorQuotationInvoiceDetail.getAlternatePart().getId():
                                    vendorQuotationInvoiceDetail.getRequisitionItem().getDemandItem().getPart().getId(),
                            VoucherType.PI,
                            partsInvoice.getIsRejected()
                    );
                }
            } else {
                List<PartOrderItem> partOrderItemList = partOrderItemService.findByPartOrderId(partsInvoice.getPartOrderId());

                List<VendorQuotationInvoiceDetail> vendorQuotationInvoiceDetailList = partOrderItemList.stream().map(PartOrderItem::getIqItem).collect(Collectors.toList());

                for (VendorQuotationInvoiceDetail vendorQuotationInvoiceDetail : vendorQuotationInvoiceDetailList) {
                    demandStatusService.updateRejectedStatus(
                            vendorQuotationInvoiceDetail.getPoItem().getIqItem().getRequisitionItem().getDemandItem().getStoreDemand().getId(),
                            partsInvoice.getId(),
                            Objects.nonNull(vendorQuotationInvoiceDetail.getPoItem().getIqItem().getAlternatePart())?vendorQuotationInvoiceDetail.getPoItem().getIqItem().getAlternatePart().getId():
                                    vendorQuotationInvoiceDetail.getPoItem().getIqItem().getRequisitionItem().getDemandItem().getPart().getId(),
                            VoucherType.PI,
                            partsInvoice.getIsRejected()
                    );
                }
            }
        }
    private void validate(PartsInvoicesDto dto, PartsInvoice old) {
        List<PartsInvoice> partsInvoices = partsInvoicesRepository.findByPartOrderId(dto.getPartOrderId());
        if (!CollectionUtils.isEmpty(partsInvoices)) {
            partsInvoices.forEach(partInvoice -> {
                if (Objects.nonNull(old) && partInvoice.equals(old)) {
                    return;
                }
                throw EngineeringManagementServerException.badRequest(
                        ErrorId.PART_INVOICE_EXISTS);
            });
        }
    }

    private void updateWorkflowActionToInitialStage(PartsInvoice partsInvoice) {
        partsInvoice.setWorkFlowType(OWN_DEPARTMENT);
        partsInvoice.setSubmoduleItemId(getNextSubModuleItemId(partsInvoice.getRfqType()));
        List<WorkFlowAction> sortedWorkflowActions = workFlowActionService.getSortedWorkflowActions(Sort.Direction.ASC, getInitialSubModuleItemId(partsInvoice.getRfqType()));

        List<ApprovalStatusType> approvalStatusTypeList = Arrays.asList(PARTS_INVOICE, PARTS_INVOICE_AUDIT, PARTS_INVOICE_FINANCE);
        helper.deleteAllByParentIdAndApprovalStatusTypes(partsInvoice.getId(), approvalStatusTypeList);

        List<RemarkType> remarkTypeList = Arrays.asList(PI_OWN_DEPARTMENT_APPROVAL_REMARK, PI_FINANCE_APPROVAL_REMARK, PI_AUDIT_APPROVAL_REMARK);
        helper.deleteByParentIdAndRemarkTypeIn(partsInvoice.getId(), remarkTypeList);

        approvalStatusService.create(ApprovalStatusDto.of(partsInvoice.getId(), PARTS_INVOICE, workFlowActionService.getByIndex(INITIAL_ORDER, sortedWorkflowActions)));
        partsInvoice.setWorkFlowAction(workFlowActionService.getByIndex(INITIAL_ORDER + INT_ONE, sortedWorkflowActions));
    }
}
