package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Alert Level ViewModel
 *
 * @author Nafiul Islam
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlertLevelListViewModel {

    private Long id;
    private Integer year;
    private Integer month;
    private String aircraftModelName;
    private String aircraftLocationName;
    private Double alertLevel;
}
