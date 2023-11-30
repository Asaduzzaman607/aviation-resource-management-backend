package com.digigate.engineeringmanagement.storeinspector.payload.response.storeinspector;

import com.digigate.engineeringmanagement.storeinspector.constant.InspectionCriterionStatus;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InspectionCriterionResponseDto {
    private Long id;
    private Long inspectionId;
    private Long descriptionId;
    private String description;
    private InspectionCriterionStatus inspectionStatus;
    private Boolean isActive;
}
