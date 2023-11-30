package com.digigate.engineeringmanagement.procurementmanagement.dto.response;

import com.digigate.engineeringmanagement.configurationmanagement.constant.VendorType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QrVendorViewModel {
    private Long id;
    private Long quoteRequestId;
    private LocalDate requestDate;
    private Long vendorId;
    private String vendorName;
    private VendorType vendorType;
    private String vendorWorkFlowName;
}
