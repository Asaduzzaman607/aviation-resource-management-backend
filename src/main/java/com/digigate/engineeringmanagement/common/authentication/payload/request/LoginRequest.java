package com.digigate.engineeringmanagement.common.authentication.payload.request;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.SwaggerDefinition;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
public class LoginRequest {
    @ApiModelProperty(value = "login", example = "superadmin")
    @NotBlank(message = ErrorId.LOGIN_IS_REQUIRED)
    private String login;

    @ApiModelProperty(value = "password", example = "SUPER_ADMIN")
    @NotBlank(message = ErrorId.PASSWORD_IS_REQUIRED)
    private String password;
}
