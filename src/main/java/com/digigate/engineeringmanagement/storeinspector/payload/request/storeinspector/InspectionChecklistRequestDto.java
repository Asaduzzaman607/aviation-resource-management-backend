package com.digigate.engineeringmanagement.storeinspector.payload.request.storeinspector;

import com.digigate.engineeringmanagement.common.payload.IDto;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InspectionChecklistRequestDto implements IDto {
    @NotBlank
    @Size(max = 8000)
    private String description;

}