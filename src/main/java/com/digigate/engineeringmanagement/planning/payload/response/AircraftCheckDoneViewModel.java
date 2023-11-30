package com.digigate.engineeringmanagement.planning.payload.response;

import com.digigate.engineeringmanagement.planning.constant.CheckType;
import lombok.*;

import java.time.LocalDate;

/**
 * AircraftCheckDone ViewModel
 *
 * @author Nafiul Islam
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AircraftCheckDoneViewModel {

    private Long id;
    private Long aircraftId;
    private String aircraftName;
    private Double aircraftCheckDoneHour;
    private LocalDate aircraftCheckDoneDate;
    private Boolean isActive;

    private CheckType checkTypeEnum;

    public AircraftCheckDoneViewModel(Long id, Long aircraftId, String aircraftName, Double aircraftCheckDoneHour,
                                      LocalDate aircraftCheckDoneDate, Boolean isActive, CheckType checkType) {
        this.id = id;
        this.aircraftId = aircraftId;
        this.aircraftName = aircraftName;
        this.aircraftCheckDoneHour = aircraftCheckDoneHour;
        this.aircraftCheckDoneDate = aircraftCheckDoneDate;
        this.isActive = isActive;
        this.checkTypeEnum = checkType;
    }


    private String checkType;
}
