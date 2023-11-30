package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

import java.util.Date;
import java.util.Set;

/**
 * AircraftCheckIndex ViewModel
 *
 * @author Ashraful
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AircraftCheckIndexViewModel {

    private Long id;

    private Long aircraftId;

    private String aircraftName;

    private Long woId;

    private String  woNo;

    private Date doneDate;

    private Double doneHour;

    private Integer doneCycle;

    private Boolean isActive;

    private Set<AircraftCheckForAircraftViewModel> aircraftTypeCheckSet;

    private Set<LdndForTaskViewModel> ldndForTaskViewModelSet;
}
