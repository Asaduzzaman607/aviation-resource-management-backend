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
public class ConfigSubmoduleItemDto implements IDto {
    private Long id;
    @NotNull(message = ErrorId.SUB_MODULE_ID_REQUIRED)
    private Long subModuleId;
    @NotBlank(message = ErrorId.ITEM_NAME_REQUIRED)
    private String itemName;
    private String urlPath;
    @NotNull(message = ErrorId.SUBMODULE_ITEM_ORDER_MUST_NOT_BE_EMPTY)
    private int order;
    private Boolean isBase;
    private Long baseItem;
    private Boolean isActive;
}
