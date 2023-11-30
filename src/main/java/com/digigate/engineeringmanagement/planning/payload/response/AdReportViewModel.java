package com.digigate.engineeringmanagement.planning.payload.response;

import com.digigate.engineeringmanagement.planning.constant.EffectivityType;
import com.digigate.engineeringmanagement.planning.constant.TaskStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Objects;

/**
 * AdReport ViewModel
 *
 * @author Ashraful
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdReportViewModel {
    private Long id;
    private String adNo;
    private String sbNo;
    private String description;
    private LocalDate effectiveDate;
    private Double thresholdHour;
    private Integer thresholdCycle;
    private Integer thresholdDay;
    private EffectivityType applicability;

    private TaskStatusEnum taskStatus;

    private TaskStatusEnum status;
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

    private Long partId;

    private String serialNo;

    public AdReportViewModel(Long id, String adNo, String sbNo, String description, LocalDate effectiveDate,
                             Double thresholdHour, Integer thresholdCycle, Integer thresholdDay,
                             EffectivityType applicability, TaskStatusEnum taskStatus, TaskStatusEnum status,
                             Double intervalHour, Integer intervalCycle, Integer intervalDay, Double lastDoneFlyingHour,
                             Integer lastDoneFlyingCycle, LocalDate lastDoneDate, Double nextDueFlyingHour,
                             Integer nextDueFlyingCycle, LocalDate nextDueDate, Double remainingFlyingHour,
                             Integer remainingFlyingCycle, String remarks, Boolean isApuControl, Long partId,
                             String serialNo) {
        this.id = id;
        this.adNo = adNo;
        this.sbNo = sbNo;
        this.description = description;
        this.effectiveDate = effectiveDate;
        this.thresholdHour = thresholdHour;
        this.thresholdCycle = thresholdCycle;
        this.thresholdDay = thresholdDay;
        this.applicability = applicability;
        this.taskStatus = taskStatus;
        this.status = status;
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
        this.partId = partId;
        this.serialNo = serialNo;
    }

    public AdReportViewModel(Long id, String adNo, String sbNo, String description, LocalDate effectiveDate,
                             Double thresholdHour, Integer thresholdCycle, Integer thresholdDay, EffectivityType applicability,
                             TaskStatusEnum taskStatus, TaskStatusEnum status, Double intervalHour, Integer intervalCycle, Integer intervalDay,
                             Double lastDoneFlyingHour, Integer lastDoneFlyingCycle, LocalDate lastDoneDate,
                             Double nextDueFlyingHour, Integer nextDueFlyingCycle,
                             LocalDate nextDueDate, Double remainingFlyingHour, Integer remainingFlyingCycle,
                             String remarks, Boolean isApuControl) {
        this.id = id;
        this.adNo = adNo;
        this.sbNo = sbNo;
        this.description = description;
        this.effectiveDate = effectiveDate;
        this.thresholdHour = thresholdHour;
        this.thresholdCycle = thresholdCycle;
        this.thresholdDay = thresholdDay;
        this.applicability = applicability;
        this.taskStatus = taskStatus;
        this.status = status;
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

    private Long remainingDay;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof AdReportViewModel)) return false;
        return Objects.nonNull(this.getAdNo()) && Objects.nonNull(this.getId())
                && this.getAdNo().equals(((AdReportViewModel) object).getAdNo())
                && this.getId().equals(((AdReportViewModel) object).getId());
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getAdNo()) && Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getAdNo().hashCode();
    }
}
