package com.digigate.engineeringmanagement.planning.dto.request;

import com.digigate.engineeringmanagement.common.payload.SDto;
import lombok.Data;

@Data
public class SerialSearchDto implements SDto {
    private String serialNumber;
    private Long partId;
    private Boolean isActive;
}
