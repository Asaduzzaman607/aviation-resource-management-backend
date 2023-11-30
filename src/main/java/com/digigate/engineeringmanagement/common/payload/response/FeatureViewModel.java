package com.digigate.engineeringmanagement.common.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Feature view model
 *
 * @author Pranoy Das
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeatureViewModel {
    private Long featureId;
    private String featureName;
    private String urlPath;
    private Integer order;
    private Boolean isBase;
    private List<ActionViewModel> actionViewModelList;
}
