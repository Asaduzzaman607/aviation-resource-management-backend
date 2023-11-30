package com.digigate.engineeringmanagement.common.payload.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class LoginNameChangePayload {
    @NotBlank
    @Size(min = 4, max = 50)
    private String login;
}
