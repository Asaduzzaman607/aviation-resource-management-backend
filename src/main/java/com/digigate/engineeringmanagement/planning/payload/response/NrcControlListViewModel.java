package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Nrc ControlList ViewModel
 *
 * @author ashinisingha
 */

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class NrcControlListViewModel {
    private Long id;
    private Long aircraftId;
    private String aircraftName;
    private String airframeSerial;
    private String AircraftModelName;
    private Long woId;
    private String woNo;
    private Long aircraftCheckIndexId;
    private List<String> typeOfCheckList;
    private LocalDate date;
}
