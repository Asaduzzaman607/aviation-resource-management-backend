package com.digigate.engineeringmanagement.planning.payload.response;

import com.digigate.engineeringmanagement.configurationmanagement.constant.ClassificationTypeEnum;
import com.digigate.engineeringmanagement.configurationmanagement.constant.IncidentTypeEnum;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Aircraft Incidents ViewModel
 *
 * @author Nafiul Islam
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AircraftIncidentsViewModel {

    private Long id;
    private Long aircraftId;
    private String aircraftName;
    private LocalDate date;
    private IncidentTypeEnum incidentTypeEnum;
    private ClassificationTypeEnum classificationTypeEnum;
    private String incidentDesc;
    private String actionDesc;
    private String referenceAtl;
    private String seqNo;
    private String remarks;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
