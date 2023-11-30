package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LdndViewModel {

    private LocalDate dueDate;
    private Double dueHour;
    private Integer dueCycle;

    private Long remainingDay;
    private Double remainingHour;
    private Integer remainingCycle;
    private LocalDate estimatedDueDate;
}
