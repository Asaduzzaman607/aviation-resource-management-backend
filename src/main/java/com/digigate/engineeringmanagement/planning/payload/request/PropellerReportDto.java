package com.digigate.engineeringmanagement.planning.payload.request;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PropellerReportDto {
    @NotNull
    private Long aircraftBuildId;
    private LocalDate date;
}
