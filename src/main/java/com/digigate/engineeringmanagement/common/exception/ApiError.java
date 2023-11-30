package com.digigate.engineeringmanagement.common.exception;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ApiError {
    List<EngineeringManagementError> apiErrors;
    public void addError(EngineeringManagementError error) {
        if(apiErrors == null) {
            apiErrors = new ArrayList<>();
        }
        apiErrors.add(error);
    }
}
