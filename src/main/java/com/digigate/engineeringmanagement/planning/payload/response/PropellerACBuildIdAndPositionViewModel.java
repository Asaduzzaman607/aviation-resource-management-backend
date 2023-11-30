package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PropellerACBuildIdAndPositionViewModel {
    private Long aircraftBuildId;
    private String positionName;
}
