package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * AircraftMaintenanceViewModel
 *
 * @author Nafiul Islam
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AircraftMaintenanceViewModel {

    private Long amlId;
    private Integer pageNo;
    private Character alphabet;
    private LocalDate date;
}
