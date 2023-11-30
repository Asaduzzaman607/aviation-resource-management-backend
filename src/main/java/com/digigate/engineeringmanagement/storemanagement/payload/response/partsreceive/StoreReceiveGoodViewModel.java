package com.digigate.engineeringmanagement.storemanagement.payload.response.partsreceive;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class StoreReceiveGoodViewModel {
    private Long id;
    private LocalDate grDate;
    private Long storeStockInwardId;
    private String storeStockInwardSerialNo;
    private Long requisitionId;
    private String requisitionNo;
}
