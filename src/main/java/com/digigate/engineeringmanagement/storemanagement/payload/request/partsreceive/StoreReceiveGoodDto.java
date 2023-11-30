package com.digigate.engineeringmanagement.storemanagement.payload.request.partsreceive;

import com.digigate.engineeringmanagement.common.payload.IDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class StoreReceiveGoodDto implements IDto {
    private LocalDate grDate;
    @NotNull
    private Long storeStockInwardId;
    @NotNull
    private Long requisitionId;
}
