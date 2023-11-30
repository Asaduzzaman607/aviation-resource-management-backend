package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

/**
 * AircraftBuildExcelViewModel
 *
 * @author Nafiul Islam
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AircraftBuildExcelViewModel {
    private String aircraftName;
    private String higherModelName;
    private String higherPartNo;
    private String modelName;
    private String partNo;
    private String serialNo;
    private String positionName;
}
