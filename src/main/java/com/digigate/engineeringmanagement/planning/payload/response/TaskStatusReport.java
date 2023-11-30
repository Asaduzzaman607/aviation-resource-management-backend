package com.digigate.engineeringmanagement.planning.payload.response;

import com.digigate.engineeringmanagement.planning.constant.EffectivityType;
import com.digigate.engineeringmanagement.planning.constant.TaskStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class TaskStatusReport {
    private Long ldndId;
    private String taskNo;
    private String msnNo;
    private String description;
    private LocalDate effectiveDate;
    private LocalDate issueDate;
    private String revisionNo;
    private EffectivityType applicability;
    private TaskStatusEnum taskStatus;

    private Double thresholdHour;
    private Integer thresholdCycle;
    private Integer thresholdDay;
    private Double intervalHour;
    private Integer intervalCycle;
    private Integer intervalDay;

    private Double lastDoneFlyingHour;
    private Integer lastDoneFlyingCycle;
    private LocalDate lastDoneDate;

    private Double nextDueFlyingHour;
    private Integer nextDueFlyingCycle;
    private LocalDate nextDueDate;

    private Double remainingFlyingHour;
    private Integer remainingFlyingCycle;

    private String remarks;
    private Boolean isApuControl;

    private Long remainingDay;

    public TaskStatusReport(Long ldndId, String taskNo, String msnNo, String description, LocalDate effectiveDate,
                            LocalDate issueDate, String revisionNo, EffectivityType applicability, TaskStatusEnum taskStatus,
                            Double thresholdHour, Integer thresholdCycle, Integer thresholdDay, Double intervalHour,
                            Integer intervalCycle, Integer intervalDay, Double lastDoneFlyingHour, Integer lastDoneFlyingCycle,
                            LocalDate lastDoneDate, Double nextDueFlyingHour, Integer nextDueFlyingCycle, LocalDate nextDueDate,
                            Double remainingFlyingHour, Integer remainingFlyingCycle, String remarks, Boolean isApuControl) {
        this.ldndId = ldndId;
        this.taskNo = taskNo;
        this.msnNo = msnNo;
        this.description = description;
        this.effectiveDate = effectiveDate;
        this.issueDate = issueDate;
        this.revisionNo = revisionNo;
        this.applicability = applicability;
        this.taskStatus = taskStatus;
        this.thresholdHour = thresholdHour;
        this.thresholdCycle = thresholdCycle;
        this.thresholdDay = thresholdDay;
        this.intervalHour = intervalHour;
        this.intervalCycle = intervalCycle;
        this.intervalDay = intervalDay;
        this.lastDoneFlyingHour = lastDoneFlyingHour;
        this.lastDoneFlyingCycle = lastDoneFlyingCycle;
        this.lastDoneDate = lastDoneDate;
        this.nextDueFlyingHour = nextDueFlyingHour;
        this.nextDueFlyingCycle = nextDueFlyingCycle;
        this.nextDueDate = nextDueDate;
        this.remainingFlyingHour = remainingFlyingHour;
        this.remainingFlyingCycle = remainingFlyingCycle;
        this.remarks = remarks;
        this.isApuControl = isApuControl;
    }
}
