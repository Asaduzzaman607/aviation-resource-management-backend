package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Tech Inc ViewModel
 *
 * @author Nafiul Islam
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TechIncViewModel {
    private List<TechnicalIncidentsViewModel> technicalIncidentsViewModelList;
    private List<NonTechnicalIncidentsViewModel> nonTechnicalIncidentsViewModelList;

}
