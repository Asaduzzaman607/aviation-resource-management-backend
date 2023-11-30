package com.digigate.engineeringmanagement.planning.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * AcCancellations Search Dto
 *
 * @author Nafiul Islam
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AcCancellationsSearchDto {

    @NotNull
    private Long aircraftModelId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isActive;
}
