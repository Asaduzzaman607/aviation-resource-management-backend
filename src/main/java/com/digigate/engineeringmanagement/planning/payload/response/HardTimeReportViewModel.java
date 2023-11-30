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
public class HardTimeReportViewModel {
    private String mpdTask;
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
    private String taskType;
    private String ata;
    private String location;

    public HardTimeReportViewModel( String mpdTask, String taskDescription, Boolean isApuControl, Integer intervalDay,
                                    Double intervalHour, Integer intervalCycle, Integer thresholdDay, Double thresholdHour,
                                    Integer thresholdCycle, TaskStatusEnum status, ModelType modelType, String partNo,
                                   String serialNo, LocalDate doneDate, Double doneHour, Integer doneCycle,
                                   LocalDate dueDate, Double dueHour, Integer dueCycle, Double remainingHour,
                                   Integer remainingCycle, LocalDate estimatedDueDate,
                                   String taskType, String ata, String location) {
        this.mpdTask = mpdTask;
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
        this.taskType = taskType;
        this.ata = ata;
        this.location = location;
    }

    private Long remainingDay;
}
