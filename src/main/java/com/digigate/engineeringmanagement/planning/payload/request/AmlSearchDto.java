package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.common.payload.SDto;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class AmlSearchDto implements SDto {
    private Integer pageNo;
    private Character alphabet;
    private Long aircraftId;
    private String flightNo;
    private LocalDate fromDate;
    private LocalDate toDate;
    private Long fromAirportId;
    private Long toAirportId;
    private Boolean isActive;
    private Boolean IsDesc = true;
}
