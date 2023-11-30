package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Engine Unscheduled Removals ViewModel
 *
 * @author Nafiul Islam
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EngineUnscheduledRemovalsViewModel {
    private Integer month;
    private Integer year;
    private Integer noOfRemv;
    private Double rateByHours;
}
