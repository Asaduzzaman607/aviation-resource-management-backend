package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
 * Technical ViewModel
 *
 * @author Nafiul Islam
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TechnicalViewModel {
    private Integer month;
    private Integer year;
    private Integer takeOffAbandoned;
    private Integer returnBeforeTakeOff;
    private Integer returnAfterTakeOff;
    private Integer engineShutDownInFlight;
    private Integer fireWarningLight;
    private Integer fuelDumping;
    private Integer otherReportableDefect;
    private Integer technicalTotal;
    private Double technicalRate;
}
