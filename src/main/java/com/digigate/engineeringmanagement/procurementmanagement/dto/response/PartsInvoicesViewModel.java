package com.digigate.engineeringmanagement.procurementmanagement.dto.response;

import com.digigate.engineeringmanagement.common.payload.response.ApprovalRemarksResponseDto;
import com.digigate.engineeringmanagement.procurementmanagement.constant.InvoiceType;
import com.digigate.engineeringmanagement.procurementmanagement.constant.PartsInVoiceWorkFlowType;
import com.digigate.engineeringmanagement.procurementmanagement.entity.ComparativeStatement;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand.ApprovalStatusViewModel;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Getter
@Setter
public class PartsInvoicesViewModel {
    private Long id;
    private String invoiceNo;
    private InvoiceType invoiceType;
    private Long partOrderId;
    private String partOrderNo;
    private List<CsViewModel> csViewModelList;
    private Set<String> attachment;
    private String tac;
    private String vendorAddress;
    private String vendorEmail;
    private String vendorTelephone;
    private String vendorFax;
    private String vendorWebsite;
    private String vendorFrom;
    private String followUpBy;
    private String toFax;
    private String toTel;
    private String remark;
    private String shipTo;
    private String billTo;
    private String paymentTerms;
    private LocalDate updateDate;
    private List<VendorQuotationInvoiceDetailViewModel> vendorQuotationDetails;
    private List<VendorQuotationFeeInvoiceViewModel> vendorQuotationFees;
    private Long submittedById;
    private Long workFlowActionId;
    private Integer workflowOrder;
    private String workflowName;
    private Boolean actionEnabled;
    private Boolean editable;
    private Boolean isRejected;
    private String rejectedDesc;
    private PartsInVoiceWorkFlowType workFlowType;
    private Map<Long, ApprovalStatusViewModel> approvalStatuses;
    private Map<Long, ApprovalStatusViewModel> auditApprovalStatuses;
    private Map<Long, ApprovalStatusViewModel> financeApprovalStatuses;
    private List<ApprovalRemarksResponseDto> approvalRemarksResponseDtoList;
    private List<ApprovalRemarksResponseDto> approvalRemarksResponseDtoListAudit;
    private List<ApprovalRemarksResponseDto> approvalRemarksResponseDtoListFinance;
    private List<PartInvoiceItemResponseDto> partInvoiceItemDtoList;
}
