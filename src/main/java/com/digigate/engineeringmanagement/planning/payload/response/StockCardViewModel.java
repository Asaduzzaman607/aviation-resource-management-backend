package com.digigate.engineeringmanagement.planning.payload.response;

import com.digigate.engineeringmanagement.planning.constant.PartStatus;
import com.digigate.engineeringmanagement.planning.constant.StorePartAvailabilityLogParentType;
import com.digigate.engineeringmanagement.storemanagement.constant.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StockCardViewModel {
    private Long id;
    private String partNo;
    private String description;
    private String uomCode;
    private Long storePartSerialId;
    private PartStatus partStatus;
    private StorePartAvailabilityLogParentType parentType;
    private String rackCode;
    private String rackRowCode;
    private String rackRowBinCode;
    private String officeCode;
    private String otherLocation;
    private String aircraftModelName;
    private Integer minStock;
    private Integer maxStock;
    private Long stockRoomId;
    private String stockRoomCode;
    private Long serialId;
    private String serialNumber;
    private Long logId;
    private Double unitPrice;
    private Integer issuedQty;
    private Integer receivedQty;
    private Integer inStock;
    private String voucherNo;
    private LocalDateTime createdAt;
    private Long wfaId;
    private String workFlowActionName;
    private String submittedByUserLogin;
    private TransactionType transactionType;
    private Long parentId;
}
