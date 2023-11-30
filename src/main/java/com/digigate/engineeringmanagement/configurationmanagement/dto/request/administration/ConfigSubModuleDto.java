package com.digigate.engineeringmanagement.configurationmanagement.dto.request.administration;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.payload.IDto;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConfigSubModuleDto implements IDto {
    private Long id;
    @NotNull(message = ErrorId.MODULE_ID_REQUIRED)
    private Long moduleId;
    @NotBlank(message = ErrorId.SUB_MODULE_NAME_REQUIRED)
    private String submoduleName;
    @NotNull(message = ErrorId.SUBMODULE_ORDER_MUST_NOT_BE_EMPTY)
    private int order;
    private Boolean isActive;
}
