package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class EngineLlpPartViewModel {
    private String nomenclature;
    private String partNo;
    private String serialNo;
    private Long serialId;
    private Double installedTsn;
    private Integer installedCsn;
    private Double currentTsn;
    private Integer currentCsn;
    private Long lifeLimit;
    private Long remainingFC;
    private LocalDate estimatedDueDate;
}
