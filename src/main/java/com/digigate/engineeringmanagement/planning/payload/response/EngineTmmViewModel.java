package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class EngineTmmViewModel {
    private LocalDate currentDate;
    private Double tat;
    private Integer tac;
    private Double tsn;
    private Integer csn;
    private Double tso;
    private Integer cso;
    private String status;
}
