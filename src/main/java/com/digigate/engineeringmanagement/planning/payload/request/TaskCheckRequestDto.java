package com.digigate.engineeringmanagement.planning.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class TaskCheckRequestDto {

    @NotNull
    private Long acModelId;
    private Double thresholdHour;
    private Integer thresholdDay;

}
