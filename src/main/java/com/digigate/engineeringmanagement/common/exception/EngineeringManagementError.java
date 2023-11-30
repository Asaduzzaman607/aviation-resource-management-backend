package com.digigate.engineeringmanagement.common.exception;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EngineeringManagementError {
    private String code;
    private String message;

    public EngineeringManagementError(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
