package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.common.payload.IDto;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Aircraft Phase Request Check DTO
 *
 * @author Sayem Hasnat
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class APhaseCheckRequestDto implements IDto {
    private LocalDate doneDate;
    private Double doneFlightHour;
    private Integer doneFlightCycle;
    @NotNull
    private Long aircraftId;
}
