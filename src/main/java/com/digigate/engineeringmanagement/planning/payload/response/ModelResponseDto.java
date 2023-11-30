package com.digigate.engineeringmanagement.planning.payload.response;


import com.digigate.engineeringmanagement.planning.constant.ModelType;
import lombok.*;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ModelResponseDto {

    private Long modelId;

    private Long aircraftModelId;

    private String aircraftModelName;

    private ModelType modelType;

    private String modelName;

    private String description;

    private Set<Integer> lifeCodes;

    private Set<String> lifeCodesValue;

    private String version;

    private Boolean isActive;
}
