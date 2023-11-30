package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.common.payload.IDto;
import lombok.*;

import javax.validation.constraints.NotBlank;

/**
 * Task type dto
 *
 * @author Asifur Rahman
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskTypeDto implements IDto {

    @NotBlank
    private String name;

    private String description;
}
