package com.digigate.engineeringmanagement.planning.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Aircraft Engine Dto
 *
 * @author Pranoy Das
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AircraftEngineDto {
    @NotNull
    private Long aircraftBuildId;
    private List<@Valid EngineShopVisitDto> engineShopVisitDtoList;

    private String nameExtension;

    @NotEmpty
    private List<@Valid EngineTimeDto> engineTimeDtoList;
}
