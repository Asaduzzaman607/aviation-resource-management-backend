package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CurrentTimesViewModel {
    private EngineTmmViewModel engineTmmViewModel;
    private EngineRgbViewModel engineRgbViewModel;
    private CurrentEngineTimeViewModel currentEngineTimeViewModel;
}
