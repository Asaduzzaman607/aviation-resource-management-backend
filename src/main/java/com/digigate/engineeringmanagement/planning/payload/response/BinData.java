package com.digigate.engineeringmanagement.planning.payload.response;

import com.digigate.engineeringmanagement.configurationmanagement.dto.projection.VendorProjection;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BinData {
    private Long storePartSerialId;
    private Double tso;
    private String grn;
    private Integer issuedQty;
    private Long serialId;
    private String serialNumber;
    private Long logId;
    private Integer inStock;
    private String voucherNo;
    private LocalDateTime createdAt;
    private Integer receivedQty;
    private LocalDate selfLife;
    private String submittedUser;
    private VendorProjection vendorName;
}

