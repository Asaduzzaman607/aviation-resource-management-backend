package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.common.payload.SDto;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModelSearchDto implements SDto {

    private Integer aircraftModelId;

    private String modelName;

    private Boolean isActive;

}
