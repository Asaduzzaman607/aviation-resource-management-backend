package com.digigate.engineeringmanagement.common.payload.request;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class UserPayload {
    @Size(min = 3, max = 20, message = ErrorId.INVALID_LOGIN_SIZE)
    @NotBlank(message = ErrorId.LOGIN_IS_REQUIRED)
    private String login;
    private String password;
    @NotNull(message = ErrorId.ROLE_IS_REQUIRED)
    private Integer roleId;
    private String confirmPassword;
    private Long employeeId;
    private Boolean isActive;
}
