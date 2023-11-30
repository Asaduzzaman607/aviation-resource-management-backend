package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.common.payload.SDto;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PropellerSearchDto implements SDto {
    private String nomenClature;
    private String partNo;
    private String serialNo;
    private LocalDate estimatedStartDate;
    private LocalDate estimatedEndDate;
    private Set<Long> aircraftIds;
    private Boolean isActive;
}
