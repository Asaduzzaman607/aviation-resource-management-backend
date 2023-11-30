package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Aircraft Defect List ViewModel
 *
 * @author Nafiul Islam
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AircraftDefectListViewModel {
    private Long aircraftId;
    private Long locationId;
    private String aircraftName;
    private String locationName;
    private String systemName;
    private Long defectCount;
}
