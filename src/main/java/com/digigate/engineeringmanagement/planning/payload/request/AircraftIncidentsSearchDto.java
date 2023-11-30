package com.digigate.engineeringmanagement.planning.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;


/**
 * Aircraft Incidents Search Dto
 *
 * @author Nafiul Islam
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AircraftIncidentsSearchDto {

    @NotNull
    private Long aircraftId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isActive;
}
