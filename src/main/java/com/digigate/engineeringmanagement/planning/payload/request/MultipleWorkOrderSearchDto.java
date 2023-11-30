package com.digigate.engineeringmanagement.planning.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Multiple Work Order Search Dto
 *
 * @author Nafiul Islam
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class MultipleWorkOrderSearchDto {
    @NotNull
    private Long aircraftId;
    @NotNull
    private LocalDate fromDate;
    @NotNull
    private LocalDate toDate;
    private Boolean isActive;

}
