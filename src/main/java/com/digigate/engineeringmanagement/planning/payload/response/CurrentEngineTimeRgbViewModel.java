package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class CurrentEngineTimeRgbViewModel {
    private Double hour;
    private Integer cycle;
}
