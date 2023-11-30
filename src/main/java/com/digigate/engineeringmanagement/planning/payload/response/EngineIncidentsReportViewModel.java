package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Engine Incidents Report ViewModel
 *
 * @author Nafiul Islam
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EngineIncidentsReportViewModel {
    private List<EngineInFlightShutDownsViewModel> engineInFlightShutDownsViewModelList;
    private List<EngineUnscheduledRemovalsViewModel> engineUnscheduledRemovalsViewModelList;
    private String engineType;
}
