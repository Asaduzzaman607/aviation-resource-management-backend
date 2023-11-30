package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Engine In Flight Shu tDowns ViewModel
 *
 * @author Nafiul Islam
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EngineInFlightShutDownsViewModel {
    private Integer month;
    private Integer year;
    private Integer noOfIfsd;
    private Double rateByHours;
}
