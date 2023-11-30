package com.digigate.engineeringmanagement.planning.payload.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Engine Incidents Search Dto
 *
 * @author Nafiul Islam
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EngineIncidentsSearchDto {

    @NotNull
    private Long aircraftModelId;
    private LocalDate fromDate;
    private LocalDate toDate;
    private Boolean isActive;
}
