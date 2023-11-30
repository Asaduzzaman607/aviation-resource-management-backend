package com.digigate.engineeringmanagement.common.payload.request;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleDto {
    @NotBlank(message = ErrorId.ROLE_NAME_MUST_NOT_BE_EMPTY)
    private String name;
}
