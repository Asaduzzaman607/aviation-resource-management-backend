package com.digigate.engineeringmanagement.planning.payload.response;

import com.digigate.engineeringmanagement.planning.constant.IntervalType;
import com.digigate.engineeringmanagement.planning.constant.LifeLimitUnit;
import com.digigate.engineeringmanagement.planning.constant.ModelType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Removed Landing Gear View Model
 *
 * @author Nafiul Islam
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RemovedLandingGearViewModel {
    private String noMenClature;
    private String partNo;
    private String serialNo;
    private LocalDate installationDate;
    private Double aircraftInHour;
    private Integer aircraftInCycle;
    private Double aircraftOutHour;
    private Integer aircraftOutCycle;
    private Integer installationCsn;
    private Double installationTsn;
    private Integer installationCso;
    private Double installationTso;
    private LocalDate ohDueDate;
    private Integer ohDueCycle;
    private Integer remainingOhCycle;
    private LocalDate estimatedDueDate;
    private Long lifeLimit;
    private LifeLimitUnit lifeLimitUnit;
    private LocalDate outDate;

    public RemovedLandingGearViewModel(String noMenClature, String partNo, String serialNo, LocalDate installationDate,
                                       Double aircraftInHour, Integer aircraftInCycle, Double aircraftOutHour,
                                       Integer aircraftOutCycle, Integer installationCsn, Double installationTsn,
                                       Integer installationCso, Double installationTso, LocalDate ohDueDate,
                                       Integer ohDueCycle, LocalDate estimatedDueDate, Long lifeLimit,
                                       LifeLimitUnit lifeLimitUnit, LocalDate outDate, Integer intervalDay,
                                       Integer intervalCycle, Integer thresholdDay, Integer thresholdCycle,
                                       IntervalType intervalType) {
        this.noMenClature = noMenClature;
        this.partNo = partNo;
        this.serialNo = serialNo;
        this.installationDate = installationDate;
        this.aircraftInHour = aircraftInHour;
        this.aircraftInCycle = aircraftInCycle;
        this.aircraftOutHour = aircraftOutHour;
        this.aircraftOutCycle = aircraftOutCycle;
        this.installationCsn = installationCsn;
        this.installationTsn = installationTsn;
        this.installationCso = installationCso;
        this.installationTso = installationTso;
        this.ohDueDate = ohDueDate;
        this.ohDueCycle = ohDueCycle;
        this.estimatedDueDate = estimatedDueDate;
        this.lifeLimit = lifeLimit;
        this.lifeLimitUnit = lifeLimitUnit;
        this.outDate = outDate;
        this.intervalDay = intervalDay;
        this.intervalCycle = intervalCycle;
        this.thresholdDay = thresholdDay;
        this.thresholdCycle = thresholdCycle;
        this.intervalType = intervalType;
    }

    private ModelType modelType;

    public RemovedLandingGearViewModel(String noMenClature, String partNo, String serialNo, LocalDate installationDate,
                                       Double aircraftInHour, Integer aircraftInCycle, Double aircraftOutHour,
                                       Integer aircraftOutCycle, Integer installationCsn, Double installationTsn,
                                       Integer installationCso, Double installationTso, LocalDate ohDueDate,
                                       Integer ohDueCycle, LocalDate estimatedDueDate, Long lifeLimit,
                                       LifeLimitUnit lifeLimitUnit, LocalDate outDate,
                                       ModelType modelType, Integer intervalDay, Integer intervalCycle,
                                       Integer thresholdDay, Integer thresholdCycle, IntervalType intervalType) {
        this.noMenClature = noMenClature;
        this.partNo = partNo;
        this.serialNo = serialNo;
        this.installationDate = installationDate;
        this.aircraftInHour = aircraftInHour;
        this.aircraftInCycle = aircraftInCycle;
        this.aircraftOutHour = aircraftOutHour;
        this.aircraftOutCycle = aircraftOutCycle;
        this.installationCsn = installationCsn;
        this.installationTsn = installationTsn;
        this.installationCso = installationCso;
        this.installationTso = installationTso;
        this.ohDueDate = ohDueDate;
        this.ohDueCycle = ohDueCycle;
        this.estimatedDueDate = estimatedDueDate;
        this.lifeLimit = lifeLimit;
        this.lifeLimitUnit = lifeLimitUnit;
        this.outDate = outDate;
        this.modelType = modelType;
        this.intervalDay = intervalDay;
        this.intervalCycle = intervalCycle;
        this.thresholdDay = thresholdDay;
        this.thresholdCycle = thresholdCycle;
        this.intervalType = intervalType;
    }

    private Long remainingOhDay;
    private Double currentTsn;
    private Integer currentCsn;
    private Double currentTso;
    private Integer currentCso;
    private Double remainingDiscard;

    private Integer intervalDay;
    private Integer intervalCycle;
    private Integer thresholdDay;
    private Integer thresholdCycle;
    private IntervalType intervalType;
}
