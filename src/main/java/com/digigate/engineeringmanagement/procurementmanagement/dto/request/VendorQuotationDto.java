package com.digigate.engineeringmanagement.procurementmanagement.dto.request;

import com.digigate.engineeringmanagement.common.payload.IDto;
import com.digigate.engineeringmanagement.procurementmanagement.constant.InputType;
import com.digigate.engineeringmanagement.procurementmanagement.constant.RfqType;
import com.digigate.engineeringmanagement.procurementmanagement.entity.PartOrder;
import com.digigate.engineeringmanagement.procurementmanagement.entity.QuoteRequest;
import com.digigate.engineeringmanagement.procurementmanagement.entity.QuoteRequestVendor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VendorQuotationDto implements IDto {
    private Long id;
    private Long quoteRequestId;
    @NotNull
    private LocalDate date;
    private LocalDate validUntil;
    private Long quoteRequestVendorId;
    private Long vendorId;
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
    private RfqType rfqType = RfqType.PROCUREMENT;
    private InputType inputType = InputType.CS;

    @Valid
    @NotEmpty
    private List<VendorQuotationInvoiceDetailDto> vendorQuotationDetails;
    @Valid
    private List<VendorQuotationFeeInvoiceDto> vendorQuotationFees;

    @JsonIgnore
    private QuoteRequest quoteRequest;
    @JsonIgnore
    private QuoteRequestVendor quoteRequestVendor;
}
