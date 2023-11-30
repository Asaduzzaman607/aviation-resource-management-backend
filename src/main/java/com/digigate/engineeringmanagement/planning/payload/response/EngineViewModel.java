package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class EngineViewModel {
    private Long aircraftBuildId;
    private String position;
    private Long partId;
    private Long serialId;
    private String serialNo;
}
