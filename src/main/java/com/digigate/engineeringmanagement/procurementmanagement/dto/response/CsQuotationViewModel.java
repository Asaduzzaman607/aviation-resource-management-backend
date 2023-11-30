package com.digigate.engineeringmanagement.procurementmanagement.dto.response;

import com.digigate.engineeringmanagement.configurationmanagement.constant.VendorType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CsQuotationViewModel {
    private Long id;
    private String quotationNo;
    private LocalDate date;
    private LocalDate validUntil;
    private String vendorName;
    private VendorType vendorType;
}
