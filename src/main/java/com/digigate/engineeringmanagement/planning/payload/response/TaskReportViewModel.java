package com.digigate.engineeringmanagement.planning.payload.response;

import com.digigate.engineeringmanagement.planning.constant.ModelType;
import com.digigate.engineeringmanagement.planning.constant.TaskStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskReportViewModel {
    private Long taskId;
    private Long aircraftId;
    private String ampTaskNo;
    private String taskSource;
    private Double manHours;
    private String taskDescription;
    private Boolean isApuControl;
    private Integer intervalDay;
    private Double intervalHour;
    private Integer intervalCycle;
    private Integer thresholdDay;
    private Double thresholdHour;
    private Integer thresholdCycle;
    private TaskStatusEnum status;
    private ModelType modelType;
    private String remark;
    private Long ldndId;
    private String partNo;
    private String serialNo;
    private LocalDate doneDate;
    private Double doneHour;
    private Integer doneCycle;
    private LocalDate dueDate;
    private Double dueHour;
    private Integer dueCycle;
    private Double remainingHour;
    private Integer remainingCycle;
    private LocalDate estimatedDueDate;
    private Boolean isActive;

    private String taskType;
    private String position;

    public TaskReportViewModel(Long taskId, Long aircraftId, String ampTaskNo, String taskSource, Double manHours,
                               String taskDescription, Boolean isApuControl, Integer intervalDay, Double intervalHour,
                               Integer intervalCycle, Integer thresholdDay, Double thresholdHour, Integer thresholdCycle,
                               TaskStatusEnum status, ModelType modelType, String remark, Long ldndId, String partNo,
                               String serialNo, LocalDate doneDate, Double doneHour, Integer doneCycle, LocalDate dueDate,
                               Double dueHour, Integer dueCycle, Double remainingHour, Integer remainingCycle,
                               LocalDate estimatedDueDate, Boolean isActive, String taskType, String position) {
        this.taskId = taskId;
        this.aircraftId = aircraftId;
        this.ampTaskNo = ampTaskNo;
        this.taskSource = taskSource;
        this.manHours = manHours;
        this.taskDescription = taskDescription;
        this.isApuControl = isApuControl;
        this.intervalDay = intervalDay;
        this.intervalHour = intervalHour;
        this.intervalCycle = intervalCycle;
        this.thresholdDay = thresholdDay;
        this.thresholdHour = thresholdHour;
        this.thresholdCycle = thresholdCycle;
        this.status = status;
        this.modelType = modelType;
        this.remark = remark;
        this.ldndId = ldndId;
        this.partNo = partNo;
        this.serialNo = serialNo;
        this.doneDate = doneDate;
        this.doneHour = doneHour;
        this.doneCycle = doneCycle;
        this.dueDate = dueDate;
        this.dueHour = dueHour;
        this.dueCycle = dueCycle;
        this.remainingHour = remainingHour;
        this.remainingCycle = remainingCycle;
        this.estimatedDueDate = estimatedDueDate;
        this.isActive = isActive;
        this.taskType = taskType;
        this.position = position;
    }

    public TaskReportViewModel(String ampTaskNo, Long ldndId, LocalDate doneDate,
                               Double doneHour, Integer doneCycle, String remark, Boolean isActive) {
        this.ampTaskNo = ampTaskNo;
        this.ldndId = ldndId;
        this.doneDate = doneDate;
        this.doneHour = doneHour;
        this.doneCycle = doneCycle;
        this.isActive = isActive;
        this.remark = remark;
    }
    private Long remainingDay;
}
