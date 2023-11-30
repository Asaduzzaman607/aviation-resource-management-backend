package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.common.payload.IDto;
import com.digigate.engineeringmanagement.planning.constant.ModelType;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModelDto implements IDto {

    @NotNull
    private Long aircraftModelId;

    @NotNull
    private ModelType modelType;

    @NotBlank
    private String modelName;

    private String description;

    private Set<Integer> lifeCodes;

    private String version;
}
