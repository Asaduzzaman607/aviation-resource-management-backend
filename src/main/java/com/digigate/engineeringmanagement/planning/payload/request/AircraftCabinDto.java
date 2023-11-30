package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.common.payload.IDto;
import lombok.*;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class AircraftCabinDto implements IDto {
    private Long aircraftCabinId;
    @NotNull
    private Long cabinId;
    @NotNull
    private Long aircraftId;
    @NotNull
    private Integer noOfSeats;
}
