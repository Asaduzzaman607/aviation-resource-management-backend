package com.digigate.engineeringmanagement.procurementmanagement.dto.response;

import com.digigate.engineeringmanagement.configurationmanagement.constant.VendorType;
import com.digigate.engineeringmanagement.procurementmanagement.constant.InputType;
import com.digigate.engineeringmanagement.procurementmanagement.constant.RfqType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
public class VendorQuotationViewModel {
    private Long id;
    private Long quoteRequestId;
    private String quoteRequestNo;
    private LocalDate quoteRequestDate;
    private String quotationNo;
    private LocalDate date;
    private LocalDate validUntil;
    private Long quoteRequestVendorId;
    private Long vendorId;
    private String vendorName;
    private VendorType vendorType;
    private String vendorAddress;
    private String vendorEmail;
    private String vendorTel;
    private String vendorFax;
    private String vendorWebsite;
    private String vendorFrom;
    private String vendorQuotationNo;
    private String toAttention;
    private String toFax;
    private String toTel;
    private Set<String> attachments;
    private String remark;
    private Boolean quoteStatus;
    private String termsCondition;
    private RfqType rfqType;
    private InputType inputType;
    private List<VendorQuotationInvoiceDetailViewModel> vendorQuotationDetails;
    private List<VendorQuotationFeeInvoiceViewModel> vendorQuotationFees;
    private PartOrderLiteDto parentOrder;
}
