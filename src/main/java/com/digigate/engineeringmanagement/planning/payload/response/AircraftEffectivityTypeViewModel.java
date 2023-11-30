package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AircraftEffectivityTypeViewModel {
    private Long aircraftId;
    private String aircraftName;
}
