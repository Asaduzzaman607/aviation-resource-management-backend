package com.digigate.engineeringmanagement.planning.dto.request;

import com.digigate.engineeringmanagement.common.payload.SDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskTypeSearchDto implements SDto {

    @NotBlank
    private String name;
    private Boolean isActive;
}
