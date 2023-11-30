package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Location ViewModel
 *
 * @author Nafiul Islam
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocationViewModel {
    private Long locationId;
    private String systemName;
    private String locationName;
    private Long total;
    private Double rate;
    private Double alertLevel;
    private List<AircraftDefectListViewModel> aircraftDefectListViewModelList;
}
