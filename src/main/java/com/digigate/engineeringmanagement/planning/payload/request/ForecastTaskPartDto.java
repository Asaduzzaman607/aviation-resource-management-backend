package com.digigate.engineeringmanagement.planning.payload.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class ForecastTaskPartDto {
    private Long id;
    private Long partId;
    private String partNo;
    private String description;
    @NotNull
    private Long quantity;
    private String ipcRef;
    @JsonIgnore
    private Long forecastTaskId;

    public ForecastTaskPartDto(Long id, Long partId, Long quantity, String ipcRef, Long forecastTaskId) {
        this.id = id;
        this.partId = partId;
        this.forecastTaskId = forecastTaskId;
        this.quantity = quantity;
        this.ipcRef = ipcRef;
    }
}
