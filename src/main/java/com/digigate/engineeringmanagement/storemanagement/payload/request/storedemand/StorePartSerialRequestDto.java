package com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand;

import com.digigate.engineeringmanagement.common.payload.IDto;
import com.digigate.engineeringmanagement.planning.constant.PartStatus;
import com.digigate.engineeringmanagement.planning.constant.StorePartAvailabilityLogParentType;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StorePartSerialRequestDto implements IDto {
    @NotNull
    private Long availId;
    @NotNull
    private Long serialId;
    private Double price = 0.0;
    private Long currencyId;
    private LocalDate rackLife;
    private LocalDate selfLife;
    private String grnNo;
    private PartStatus partStatus;
    private String shelfLifeType;
    @Min(value = 1)
    private Integer quantity = 1;
    private StorePartAvailabilityLogParentType parentType;
    private boolean issued = false;
    private Long uomId;
}
