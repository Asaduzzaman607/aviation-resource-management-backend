package com.digigate.engineeringmanagement.storemanagement.payload.request;

import com.digigate.engineeringmanagement.planning.constant.StorePartAvailabilityLogParentType;
import com.digigate.engineeringmanagement.planning.entity.Part;
import com.digigate.engineeringmanagement.storemanagement.constant.TransactionType;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.StorePartSerial;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PartAvailUpdateInternalDto {
    private Integer quantity;
    private String voucherNo;
    private StorePartSerial partSerial;
    private TransactionType transactionType;
    private StorePartAvailabilityLogParentType parentType;
    private Long parentId;
    private Double unitPrice;
    private Long currencyId;
    private String grnNo;
    private Long submittedBy;
    private Long finalUser;
    private Long vendorId;
}
