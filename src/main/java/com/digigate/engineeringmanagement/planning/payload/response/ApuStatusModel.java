package com.digigate.engineeringmanagement.planning.payload.response;

import com.digigate.engineeringmanagement.planning.constant.LifeLimitUnit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Apu Status Model
 *
 * @author Nafiul Islam
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ApuStatusModel {

    private String noMenClature;
    private String partNo;
    private String serialNo;
    private LocalDate installationDate;
    private Double aircraftInHour;
    private Integer aircraftInCycle;
    private Integer installationCsn;
    private Double installationTsn;
    private LocalDate dueDate;
    private Double dueHour;
    private Integer dueCycle;
    private Double remainingHour;
    private Integer remainingCycle;
    private LocalDate estimatedDueDate;
    private Long lifeLimit;
    private LifeLimitUnit lifeLimitUnit;

}
