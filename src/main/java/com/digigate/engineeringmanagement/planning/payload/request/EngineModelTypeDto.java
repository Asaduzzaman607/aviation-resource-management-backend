package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EngineModelTypeDto {
    private Integer id;
    @NotBlank(message = ErrorId.ENGINE_MODEL_TYPE_NAME_MUST_NOT_BE_EMPTY)
    private String name;
    private String description;
    private Boolean isActive;
}
