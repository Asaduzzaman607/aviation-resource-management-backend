package com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand;

import com.digigate.engineeringmanagement.common.payload.IDto;
import com.digigate.engineeringmanagement.storemanagement.constant.LocationTag;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StorePartAvailabilityRequestDto implements IDto {
    @NotNull
    private Long partId;
    @NotNull
    private Long officeId;
    private Long roomId;
    private Long rackId;
    private Long rackRowId;
    private Long rackRowBinId;
    private String otherLocation;
    private LocationTag locationTag;
    private Integer minStock = 0;
    private Integer maxStock = 0;
    private Long storeStockRoomId;
}
