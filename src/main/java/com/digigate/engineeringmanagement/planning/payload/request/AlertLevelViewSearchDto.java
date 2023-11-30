package com.digigate.engineeringmanagement.planning.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Alert Level View Search Dto
 *
 * @author Nafiul Islam
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AlertLevelViewSearchDto {

    @NotNull
    private Long locationId;
    @NotNull
    private Long aircraftModelId;
    @NotNull
    private LocalDate fromDate;
    @NotNull
    private LocalDate toDate;
}
