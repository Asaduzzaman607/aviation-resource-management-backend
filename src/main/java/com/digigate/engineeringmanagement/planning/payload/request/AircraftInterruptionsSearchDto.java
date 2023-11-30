package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.common.payload.SDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Aircraft Interruptions Search Dto
 *
 * @author Nafiul Islam
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AircraftInterruptionsSearchDto implements SDto {

    private Long aircraftId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isActive;
}
