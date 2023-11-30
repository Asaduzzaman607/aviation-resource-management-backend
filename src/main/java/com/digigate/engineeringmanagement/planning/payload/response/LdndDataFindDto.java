package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LdndDataFindDto {

    @NotEmpty
    private List<Long> aircraftIds;
    @NotNull
    private LocalDate fromDate;
    @NotNull
    private LocalDate toDate;
}
