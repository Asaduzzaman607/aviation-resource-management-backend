package com.digigate.engineeringmanagement.planning.payload.request;


import com.digigate.engineeringmanagement.common.payload.SDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class AircraftBuildSearchPayload implements SDto {
    private Long aircraftId;
    private String modelName;
    private String partNo;
    private String higherModelName;
    private String higherPartNo;
    private Boolean isActive = true;
}
