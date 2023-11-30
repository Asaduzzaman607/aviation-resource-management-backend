package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.common.payload.IDto;
import com.digigate.engineeringmanagement.planning.constant.CheckType;
import lombok.*;

import javax.validation.constraints.NotNull;
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
public class AircraftCheckDoneDto implements IDto {

    @NotNull
    private Long aircraftId;
    private Double aircraftCheckDoneHour;
    private LocalDate aircraftCheckDoneDate;

    private String checkType;

}
