package com.digigate.engineeringmanagement.configurationmanagement.dto.response;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfigModuleResponseDto {
    private Long id;
    private String moduleName;
    private String image;
    private Integer order;
    private Boolean isActive;
}
