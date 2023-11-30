package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EngineInstallationInfoViewModel {
    private String aircraftRegistrationNo;
    private String msn;
    private String positionName;
    private LocalDate attachDate;
    private Double tat;
    private Integer tac;
    private EngineTmmViewModel engineTmmViewModel;
    private EngineRgbViewModel engineRgbViewModel;
    private Integer averageCycles;
}
