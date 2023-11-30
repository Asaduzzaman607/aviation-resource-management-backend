package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Flight data info View Model
 *
 * @author Nafiul Islam
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FlightDataInfoViewModel {
    private Double grandTotalAirTime;
    private Integer grandTotalLanding;
    private LocalDate updateDate;
}
