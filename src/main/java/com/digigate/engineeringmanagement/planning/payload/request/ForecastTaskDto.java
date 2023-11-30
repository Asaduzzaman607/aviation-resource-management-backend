package com.digigate.engineeringmanagement.planning.payload.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class ForecastTaskDto {
    private Long id;

    @NotNull
    private Long ldndId;

    private Long taskId;

    private String taskNo;

    @NotNull
    private LocalDate dueDate;

    private String comment;

    @Valid
    Set<ForecastTaskPartDto> forecastTaskPartDtoList;

    @JsonIgnore
    private Long forecastAircraftId;

    public void addForecastPartDto(ForecastTaskPartDto forecastTaskPartDto) {
        if (Objects.isNull(forecastTaskPartDtoList)) {
            forecastTaskPartDtoList = new HashSet<>();
        }
        forecastTaskPartDtoList.add(forecastTaskPartDto);
    }

    public ForecastTaskDto(Long id, Long ldndId, Long taskId, String taskNo, LocalDate dueDate, String comment,
                           Long forecastAircraftId) {
        this.id = id;
        this.ldndId = ldndId;
        this.dueDate = dueDate;
        this.comment = comment;
        this.forecastAircraftId = forecastAircraftId;
        this.taskId = taskId;
        this.taskNo = taskNo;
    }
}
