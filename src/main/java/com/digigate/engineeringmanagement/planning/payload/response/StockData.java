package com.digigate.engineeringmanagement.planning.payload.response;

import com.digigate.engineeringmanagement.configurationmanagement.dto.projection.VendorProjection;
import com.digigate.engineeringmanagement.planning.constant.PartStatus;
import com.digigate.engineeringmanagement.planning.constant.StorePartAvailabilityLogParentType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockData {
    private Long storePartSerialId;
    private PartStatus partStatus;
    private StorePartAvailabilityLogParentType parentType;
    private String otherLocation;
    private Long serialId;
    private String serialNumber;
    private Long logId;
    private Double unitPrice;
    private Integer issuedQty;
    private Integer receivedQty;
    private Integer inStock;
    private String voucherNo;
    private LocalDateTime createdAt;
    private String submittedUser;
    private VendorProjection vendorName;
}
