package com.digigate.engineeringmanagement.common.payload.request;

import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Setter
@Getter
public class PasswordResetPayload {
    @NotBlank
    private String previousPassword;

    @NotBlank
    @Size(min = 6, max = 32)
    @Pattern(regexp = "^[a-zA-Z0-9]+$")
    private String newPassword;
}
