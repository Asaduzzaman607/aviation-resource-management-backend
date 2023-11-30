package com.digigate.engineeringmanagement.storeinspector.payload.request.storeinspector;

import com.digigate.engineeringmanagement.common.payload.IDto;
import com.digigate.engineeringmanagement.storeinspector.constant.InspectionCriterionStatus;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InspectionCriterionRequestDto implements IDto {
    private Long id;
    @NotNull
    private Long descriptionId;
    @NotNull
    private InspectionCriterionStatus inspectionStatus;
    private Boolean isActive = true;
}
