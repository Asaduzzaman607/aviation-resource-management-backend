package com.digigate.engineeringmanagement.planning.payload.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class WorkOrderAirCraftViewModel {
    private Double totalAcHours;
    private StringBuilder woNo;
    private Integer totalAcLanding;
    private String airframeSerial;
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate asOfDate;
}
