package com.digigate.engineeringmanagement.planning.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpStatSearchDto {

    @NotNull
    private Long aircraftModelId;
    @NotNull
    private LocalDate fromDate;
    @NotNull
    private LocalDate toDate;


}
