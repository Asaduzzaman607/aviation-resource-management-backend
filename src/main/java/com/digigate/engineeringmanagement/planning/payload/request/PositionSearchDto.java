package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.common.payload.SDto;
import lombok.Data;

@Data
public class PositionSearchDto implements SDto {
    private String name;
    private Boolean isActive;
}
