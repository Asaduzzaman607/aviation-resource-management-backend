package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LdndReport {

    private Long id;
    private Long taskId;
    private Long aircraftId;
    private LocalDate doneDate;
    private Double doneHour;
    private Integer doneCycle;
    private LocalDate dueDate;
    private Double dueHour;
    private Integer dueCycle;
    private Long remainingDay;
    private Double remainingHour;
    private Integer remainingCycle;
    private LocalDate estimatedDueDate;

    public LdndReport(Long id, Long taskId, Long aircraftId, LocalDate doneDate,
                      Double doneHour, Integer doneCycle, LocalDate dueDate,
                      Double dueHour, Integer dueCycle, Long remainingDay,
                      Double remainingHour, Integer remainingCycle, LocalDate estimatedDueDate) {
        this.id = id;
        this.taskId = taskId;
        this.aircraftId = aircraftId;
        this.doneDate = doneDate;
        this.doneHour = doneHour;
        this.doneCycle = doneCycle;
        this.dueDate = dueDate;
        this.dueHour = dueHour;
        this.dueCycle = dueCycle;
        this.remainingDay = remainingDay;
        this.remainingHour = remainingHour;
        this.remainingCycle = remainingCycle;
        this.estimatedDueDate = estimatedDueDate;
    }

    private String ampTaskNo;
    private String taskSource;
    private Double manHours;
    private String taskDescription;
    private Boolean isApuControl;
    private Integer intervalDay;
    private Double intervalHour;
    private Integer intervalCycle;
    private Integer thDay;
    private Double thHour;
    private Integer thCycle;
    private String remark;
    private Integer status;


    private Long positionId;
    private String position;
    private String jobProcedure;
}
