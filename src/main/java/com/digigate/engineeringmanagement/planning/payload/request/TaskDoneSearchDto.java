package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.common.payload.SDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * Task Done Search Payload
 *
 * @author Asifur Rahman
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskDoneSearchDto implements SDto {
    private String taskNo;
    private Long aircraftId;
    private String remark;
    private Boolean isActive;
}
