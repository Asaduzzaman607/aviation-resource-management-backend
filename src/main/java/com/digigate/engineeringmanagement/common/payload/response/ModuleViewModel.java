package com.digigate.engineeringmanagement.common.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Module view model
 *
 * @author Pranoy Das
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModuleViewModel {
    private Long moduleId;
    private String moduleName;
    private Integer order;
    private String image;
    List<SubModuleViewModel> subModuleList;
}
