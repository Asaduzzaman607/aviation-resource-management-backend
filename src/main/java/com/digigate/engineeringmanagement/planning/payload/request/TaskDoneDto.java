package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.common.payload.IDto;
import com.digigate.engineeringmanagement.planning.constant.IntervalType;
import com.digigate.engineeringmanagement.planning.constant.TaskStatusEnum;
import lombok.*;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Task Done Request Payload
 *
 * @author Asifur Rahman
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDoneDto implements IDto {

    @NotNull
    private Long taskId;

    @NotNull
    private Long aircraftId;

    @NotNull
    private Long partId;

    private Long positionId;

    @NotNull
    private Long serialId;

    @NotNull
    private LocalDate doneDate;

    @NotNull
    private Boolean isApuControl;
    private Double doneHour;
    private Integer doneCycle;
    private Double initialHour;
    private Integer initialCycle;
    @NotNull
    private IntervalType intervalType;

    private LocalDate dueDate;
    private Double dueHour;
    private Integer dueCycle;

    private Long remainingDay;
    private Double remainingHour;
    private Integer remainingCycle;
    private LocalDate estimatedDueDate;
    private String remark;
    private TaskStatusEnum taskStatus;
}
