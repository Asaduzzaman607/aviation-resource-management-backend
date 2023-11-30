package com.digigate.engineeringmanagement.procurementmanagement.dto.request;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.payload.IDto;
import com.digigate.engineeringmanagement.procurementmanagement.constant.InvoiceType;
import com.digigate.engineeringmanagement.procurementmanagement.constant.PartsInVoiceWorkFlowType;
import com.digigate.engineeringmanagement.procurementmanagement.constant.RfqType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PartsInvoicesDto implements IDto {
    private String invoiceNo;
    private InvoiceType invoiceType;
    private Long partOrderId;
    private Set<String> attachment;
    private String tac;
    private String vendorAddress;
    @Email(regexp = ApplicationConstant.EMAIL_VALIDATION_REGEX, message = ErrorId.INVALID_EMAIL_PATTERN)
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
    private RfqType rfqType;
    private PartsInVoiceWorkFlowType partsInVoiceWorkFlowType;
    @Valid
    private List<VendorQuotationInvoiceDetailDto> vendorQuotationDetails;
    @Valid
    private List<VendorQuotationFeeInvoiceDto> vendorQuotationFees;
    private List<PartsInvoiceItemReqDto> partInvoiceItemDtoList;
}
