package com.digigate.engineeringmanagement.planning.payload.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class ForecastAircraftDto {
    private Long id;
    @NotNull
    private Long aircraftId;
    private String aircraftName;
    private String aircraftSerial;
    @Valid
    Set<ForecastTaskDto> forecastTaskDtoList;
    @JsonIgnore
    private Long forecastId;

    public void addForecastTaskDto(ForecastTaskDto forecastTaskDto) {
        if (Objects.isNull(forecastTaskDtoList)) {
            forecastTaskDtoList = new HashSet<>();
        }
        forecastTaskDtoList.add(forecastTaskDto);
    }

    public ForecastAircraftDto(Long id, Long aircraftId, Long forecastId) {
        this.id = id;
        this.aircraftId = aircraftId;
        this.forecastId = forecastId;
    }
}
