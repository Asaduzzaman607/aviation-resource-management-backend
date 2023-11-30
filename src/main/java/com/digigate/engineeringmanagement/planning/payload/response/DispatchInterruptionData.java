package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Dispatch Interruption Data
 *
 * @author Nafiul Islam
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DispatchInterruptionData {

    private Long locationId;
    private String locationName;
    private LocalDate date;
    private String aircraftName;
    private String defect;
    private String rectification;
    private Double duration;
}
