package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class CurrentEngineTimeViewModel {
    private String nameExtension;
    private CurrentEngineTimeTmmViewModel currentEngineTimeTmmViewModel;
    private CurrentEngineTimeRgbViewModel currentEngineTimeRgbViewModel;
}
