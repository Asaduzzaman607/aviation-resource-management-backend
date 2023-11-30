package com.digigate.engineeringmanagement.planning.dto.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Aircraft Apus Dto
 *
 * @author Nafiul Islam
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AircraftApusDto {

    @NotNull
    private Long aircraftId;
    private String model;
    private LocalDate date;
    private Double tsn;
    private Integer csn;
    private Double tsr;
    private Integer csr;
    private String status;
}
