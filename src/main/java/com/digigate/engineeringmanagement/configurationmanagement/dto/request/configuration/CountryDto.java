package com.digigate.engineeringmanagement.configurationmanagement.dto.request.configuration;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.payload.IDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CountryDto implements IDto {

    private Long id;
    @NotBlank
    private String name;

    @NotBlank
    private String code;

    @NotBlank
    @Pattern(regexp = "^(\\+\\d{1,3}( )?)?$", message = ErrorId.DIALING_CODE_PATTERN)
    private String dialingCode;
}
