package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ForecastViewModel {
    private Long forecastId;
    private String name;
    private LocalDateTime creationDate;
    private Boolean isActive;
}
