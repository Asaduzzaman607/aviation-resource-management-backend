package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AircraftCheckIndexForListView {
    private Long acCheckIndexId;
    private String aircraftChecksName;
}
