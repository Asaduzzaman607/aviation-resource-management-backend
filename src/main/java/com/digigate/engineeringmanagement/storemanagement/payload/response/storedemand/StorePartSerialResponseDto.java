package com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand;

import com.digigate.engineeringmanagement.common.payload.IDto;
import com.digigate.engineeringmanagement.planning.constant.PartStatus;
import com.digigate.engineeringmanagement.planning.constant.StorePartAvailabilityLogParentType;
import lombok.*;

import javax.validation.constraints.Size;
import java.time.LocalDate;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StorePartSerialResponseDto implements IDto {
    private Long id;
    private Long availId;
    private Long serialId;
    private String serialNo;
    private Long currencyId;
    private String currencyCode;
    private Double price;
    private LocalDate rackLife;
    private String shelfLifeType;
    private LocalDate selfLife;
    private String grnNo;
    private Integer quantity;
    private PartStatus partStatus;
    private StorePartAvailabilityLogParentType parentType;
    private Boolean issued;
    private Long uomId;
    @Size(max = 200)
    private String uomCode;
}
