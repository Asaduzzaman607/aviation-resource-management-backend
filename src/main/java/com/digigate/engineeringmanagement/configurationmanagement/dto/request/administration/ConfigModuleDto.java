package com.digigate.engineeringmanagement.configurationmanagement.dto.request.administration;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class ConfigModuleDto {
    private Long id;
    @NotBlank(message = ErrorId.MODULE_NAME_REQUIRED)
    private String moduleName;
    private String image;
    @NotNull(message = ErrorId.MODULE_ORDER_MUST_NOT_BE_EMPTY)
    private Integer order;
    private Boolean isActive;
}
