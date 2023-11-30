package com.digigate.engineeringmanagement.planning.payload.response;

import com.digigate.engineeringmanagement.planning.constant.PartClassification;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BinCardViewModel {
    private Long id;
    private String partNo;
    private PartClassification classification;
    private String grn;
    private String description;
    private Long storePartSerialId;
    private Double tso;
    private String rackCode;
    private String rackRowCode;
    private String rackRowBinCode;
    private String officeCode;
    private Integer issuedQty;
    private String otherLocation;
    private Long serialId;
    private String serialNumber;
    private String aircraftModelName;
    private String uomCode;
    private Long logId;
    private Integer inStock;
    private String voucherNo;
    private LocalDateTime createdAt;
    private Integer receivedQty;
    private Integer minStock;
    private LocalDate selfLife;
    private String submittedByUserLogin;
}
