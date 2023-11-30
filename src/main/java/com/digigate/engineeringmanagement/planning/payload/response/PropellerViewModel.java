package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PropellerViewModel {
    private Long id;
    private String nomenClature;
    private String partNo;
    private String serialNo;
    private LocalDate installationDate;
    private Long installationTsn;
    private Long installationTso;
    private Long currentTsn;
    private Long currentTso;
    private Integer limitMonth;
    private Integer limitFh;
    private LocalDate estimatedDate;
    private Long aircraftId;
    private Boolean isActive;
}
