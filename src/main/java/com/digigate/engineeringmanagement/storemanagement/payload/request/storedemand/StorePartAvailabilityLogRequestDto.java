package com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand;

import com.digigate.engineeringmanagement.common.payload.IDto;
import com.digigate.engineeringmanagement.planning.constant.PartStatus;
import com.digigate.engineeringmanagement.planning.constant.StorePartAvailabilityLogParentType;
import com.digigate.engineeringmanagement.storemanagement.constant.TransactionType;
import lombok.*;

import javax.persistence.Column;
import javax.validation.constraints.Min;
import java.time.LocalDate;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StorePartAvailabilityLogRequestDto implements IDto {
    private Long uomId;
    private StorePartAvailabilityLogParentType parentType;
    private Long parentId;
    private Long serialId;
    private PartStatus partStatus;
    @Min(1)
    private Integer quantity = 1;

    private LocalDate receiveDate;
    private LocalDate shelfLife;
    private LocalDate expiryDate;
    private Double unitPrice;
    private String issuedAc;
    private String location;
    private TransactionType transactionType;
    private String grnNo;
    private Integer issuedQty;
    private Integer inStock;
    private Integer receivedQty;
    private String voucherNo;
    private String partNo;
    private String serialNo;
}
