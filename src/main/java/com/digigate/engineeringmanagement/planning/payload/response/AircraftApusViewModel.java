package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

import java.time.LocalDate;

/**
 * Aircraft Apus View Model
 *
 * @author Nafiul Islam
 */
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class AircraftApusViewModel {

    private Long id;
    private Long aircraftId;
    private String aircraftName;
    private String model;
    private LocalDate date;
    private Double tsn;
    private Integer csn;
    private Double tsr;
    private Integer csr;
    private String status;

}
