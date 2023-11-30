package com.digigate.engineeringmanagement.common.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Alert Level Search Dto
 *
 * @author Nafiul Islam
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AlertLevelSearchDto {
    @NotNull
    private Long locationId;
    @NotNull
    private Long aircraftModelId;
    @NotNull
    private LocalDate fromDate;
    @NotNull
    private LocalDate toDate;
    @Min(1)
    @Max(12)
    private Integer monthRange;
}
