package com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand;

import com.digigate.engineeringmanagement.planning.constant.PartStatus;
import com.digigate.engineeringmanagement.planning.constant.StorePartAvailabilityLogParentType;
import com.digigate.engineeringmanagement.planning.entity.Part;
import com.digigate.engineeringmanagement.storemanagement.constant.TransactionType;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StorePartSerialInternalDto {
    private PartStatus partStatus;
    private StorePartAvailabilityLogParentType parentType;
    private LocalDate shelfLife;
    private LocalDate expiryDate;
    private Double unitPrice;
    private String grnNo;
    private TransactionType transactionType;
    private String partNo;
    private String serialNo;
    private Integer quantity;
    private Part part;
}
