package com.digigate.engineeringmanagement.planning.payload.response;

import com.digigate.engineeringmanagement.planning.constant.TaskStatusEnum;
import lombok.*;

import java.time.LocalDate;

/**
 * task last done view model
 *
 * @author Asifur Rahman
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDoneViewModel {
    private Long id;
    private Long taskId;
    private Long aircraftId;
    private Long procedureId;
    private Long positionId;
    private Long partId;
    private String partNo;
    private String serialNo;
    private Long serialId;
    private String taskNo;
    private String position;
    private String aircraftName;
    private Boolean isApuControl;
    private LocalDate doneDate;
    private Double doneHour;
    private Integer doneCycle;

    private Double initialHour;
    private Integer initialCycle;
    private Integer intervalType;

    private LocalDate nextDueDate;
    private Double nextDueHour;
    private Integer nextDueCycle;

    private Long remainDay;
    private Double remainHour;
    private Integer remainCycle;
    private String remark;
    private LocalDate estimatedDueDate;
    private Boolean isActive;
    private TaskStatusEnum taskStatus;
}
