package com.digigate.engineeringmanagement.configurationmanagement.dto.response.adminstration;

import com.digigate.engineeringmanagement.configurationmanagement.dto.response.IdNameResponse;
import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConfigSubModuleResponseDto {
    private Long id;
    private String submoduleName;
    private Long moduleId;
    private String moduleName;
    private int order;
    private Boolean isActive;
}
