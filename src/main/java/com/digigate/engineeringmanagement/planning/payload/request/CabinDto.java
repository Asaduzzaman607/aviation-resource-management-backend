package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.payload.IDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Cabin dto
 *
 * @author Pranoy Das
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CabinDto implements IDto {
    private Long cabinId;

    @NotNull(message = ErrorId.CABIN_CODE_IS_REQUIRED)
    private Character code;

    @NotBlank(message = ErrorId.CABIN_TITLE_IS_REQUIRED)
    private String title;

    private Boolean activeStatus;
}
