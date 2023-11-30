package com.digigate.engineeringmanagement.common.config.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExcelDataResponse {
    private HttpStatus status;
    private String successMessage;
    private List<String> errorMessages;
}
