package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.common.payload.IDto;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForecastDto implements IDto {
    private Long id;
    @NotBlank
    private String name;
    private Boolean isActive;
    @Valid
    Set<ForecastAircraftDto> forecastAircraftDtoList;
}
