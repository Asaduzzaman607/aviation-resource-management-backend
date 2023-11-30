package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;
import java.util.Set;

/**
 * AircraftCheck ViewModel
 *
 * @author Ashraful
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AircraftCheckViewModel {

    private Long id;

    private Long checkId;

    private String checkTitle;

    private String checkDescription;

    private Long aircraftModelId;

    private String aircraftModelName;

    private Double flyingHour;

    private Long flyingDay;

    private Boolean isActive;

    private Set<TaskViewModelForAcCheck> aircraftCheckTasks;
}
