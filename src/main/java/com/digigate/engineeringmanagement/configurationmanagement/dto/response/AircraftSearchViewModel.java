package com.digigate.engineeringmanagement.configurationmanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AircraftSearchViewModel {
    private Long id;
    private String aircraftModelName;
    private String aircraftName;
    private String airframeSerial;
}
