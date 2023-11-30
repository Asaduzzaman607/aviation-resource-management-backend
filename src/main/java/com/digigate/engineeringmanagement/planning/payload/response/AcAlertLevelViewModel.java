package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

import java.time.LocalDateTime;

/**
 *  Ac AlertLevel ViewModel
 *
 * @author Nafiul Islam
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcAlertLevelViewModel {

    private Long id;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private Long aircraftModelId;
    private Long locationId;
    private Integer month;
    private Integer year;
    private Double alertLevel;
}
