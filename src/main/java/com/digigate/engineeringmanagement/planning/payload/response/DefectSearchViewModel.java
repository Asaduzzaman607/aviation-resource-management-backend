package com.digigate.engineeringmanagement.planning.payload.response;

import com.digigate.engineeringmanagement.planning.constant.DefectType;
import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DefectSearchViewModel {

    private Long id;
    private Long aircraftId;
    private String aircraftName;
    private LocalDate date;
    private DefectType defectType;
    private String defectDesc;
    private String actionDesc;
    private String reference;

    private Long locationId;
    private String location;

    private Long partId;
    private String partNumber;
    private String nomenclature;
    private String system;

    private Boolean isActive;
}
