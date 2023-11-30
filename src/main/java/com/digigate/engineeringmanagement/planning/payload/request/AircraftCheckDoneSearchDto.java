package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.common.payload.SDto;
import lombok.*;

import java.time.LocalDate;

/**
 * AircraftCheckDone Dto
 *
 * @author Nafiul Islam
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AircraftCheckDoneSearchDto implements SDto {

    private Long aircraftId;
    private LocalDate date;
    private Boolean isActive;

}
