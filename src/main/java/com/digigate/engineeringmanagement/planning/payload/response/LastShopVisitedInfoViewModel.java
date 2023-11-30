package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class LastShopVisitedInfoViewModel {
    private EngineTmmViewModel engineTmmViewModel;
    private EngineRgbViewModel engineRgbViewModel;
}
