package com.digigate.engineeringmanagement.planning.payload.response;

import com.digigate.engineeringmanagement.planning.constant.EngineIncidentsEnum;
import lombok.*;

import java.time.LocalDate;

/**
 * Engine Incidents ViewModel
 *
 * @author Nafiul Islam
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EngineIncidentsViewModel {

    private Long id;
    private Long aircraftModelId;
    private String aircraftModelName;
    private EngineIncidentsEnum engineIncidentsEnum;
    private LocalDate date;
    private Boolean isActive;
}
