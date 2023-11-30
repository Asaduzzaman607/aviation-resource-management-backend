package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Non Technical Incidents ViewModel
 *
 * @author Nafiul Islam
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NonTechnicalIncidentsViewModel {
    private String aircraftName;
    private LocalDate date;
    private String referenceAtl;
    private String incidentDes;
    private String actionDes;
    private String seqNo;
    private String remarks;
}
