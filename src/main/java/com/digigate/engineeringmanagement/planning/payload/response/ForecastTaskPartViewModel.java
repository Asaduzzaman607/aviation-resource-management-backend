package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ForecastTaskPartViewModel {
    private Long forecastTaskPartId;
    private Long partId;
    private String partNo;
    private String description;
    private Long quantity;
    private String ipcRef;
}
