package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.common.payload.IDto;
import com.digigate.engineeringmanagement.configurationmanagement.constant.ClassificationTypeEnum;
import com.digigate.engineeringmanagement.configurationmanagement.constant.IncidentTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Aircraft Incidents Entity
 *
 * @author Nafiul Islam
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AircraftIncidentsDto implements IDto {

    @NotNull
    private Long aircraftId;
    @NotNull
    private LocalDate date;
    @NotNull
    private IncidentTypeEnum incidentTypeEnum;
    @NotNull
    private ClassificationTypeEnum classificationTypeEnum;
    @NotNull
    private String incidentDesc;
    @NotNull
    private String actionDesc;
    @NotNull
    private String referenceAtl;
    @NotNull
    private String seqNo;
    private String remarks;
}
