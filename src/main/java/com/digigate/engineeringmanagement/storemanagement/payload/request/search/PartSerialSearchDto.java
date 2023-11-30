package com.digigate.engineeringmanagement.storemanagement.payload.request.search;

import com.digigate.engineeringmanagement.common.payload.SDto;
import com.digigate.engineeringmanagement.planning.constant.PartStatus;
import com.digigate.engineeringmanagement.planning.constant.StorePartAvailabilityLogParentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PartSerialSearchDto implements SDto {
    private String query;
    private Long partId;
    private Long uomId;
    private Long availId;
    private PartStatus status;
    private Boolean isActive = true;
    private Boolean onlyAvailable;
}
