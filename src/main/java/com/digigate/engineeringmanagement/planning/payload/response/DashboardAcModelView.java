package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class DashboardAcModelView {
    private AircraftData aircraftData;
    private List<DueResponse> melDueList;
    private DueResponse checkA;
    private DueResponse checkC;
    private DueResponse check2Y;
    private DueResponse check4Y;
    private DueResponse check8Y;

}
