package com.digigate.engineeringmanagement.planning.payload.response;

import com.digigate.engineeringmanagement.planning.constant.DefectType;
import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DefectViewModel {

    private Long id;
    private Long aircraftId;
    private String aircraftName;

    private Long partId;
    private String partNumber;
    private String nomenclature;

    private Long locationId;
    private String location;

    private LocalDate date;

    private String reference;

    private String defectDesc;
    private String actionDesc;
    private DefectType defectType;
    private Boolean isActive;
}
