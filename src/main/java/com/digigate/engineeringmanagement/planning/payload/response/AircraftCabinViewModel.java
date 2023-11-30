package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AircraftCabinViewModel {
    private Long aircraftCabinId;
    private Long cabinId;
    private String cabinInfo;
    private Long aircraftId;
    private String aircraftName;
    private Integer numOfSeats;
    private Boolean activeStatus;
}
