package com.digigate.engineeringmanagement.planning.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AircraftCabinSearchDto {
    private Long aircraftId;
    private Long cabinId;
    private Boolean isActive = true;
}
