package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

import java.util.List;

/**
 * Aircraft Engine Tmm Rgb View Model
 *
 * @author Pranoy Das
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AircraftEngineTmmRgbViewModel {
    private Long aircraftId;
    private Long aircraftBuildId;
    private String position;
    private String aircraftName;
    private List<EngineShopVisitViewModel> engineShopVisitViewModels;
    private List<EngineTimeViewModel> engineTimeViewModels;
}
