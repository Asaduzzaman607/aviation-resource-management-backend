package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OCCMViewModel {
    private String ata;
    private String description;
    private String partNumber;
    private String serialNumber;
    private String location;
    private String taskType;
    private LocalDate installationDate;
    private Double installationFH;
    private Integer installationFC;
    private Double currentTSN;
    private Integer currentCSN;
    private Double currentTSO;
    private Integer currentCSO;
    private Double currentTSR;
    private Integer currentCSR;
    private String remarks;
}
