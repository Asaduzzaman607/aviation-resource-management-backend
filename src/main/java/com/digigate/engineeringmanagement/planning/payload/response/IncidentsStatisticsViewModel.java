package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;
/**
 * Incidents Statistics ViewModel
 *
 * @author Nafiul Islam
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IncidentsStatisticsViewModel {
    private List<TechnicalViewModel> technicalViewModel;
    private List<NonTechnicalViewModel> nonTechnicalViewModel;
}
