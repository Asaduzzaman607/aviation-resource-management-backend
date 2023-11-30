package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.common.payload.IDto;
import com.digigate.engineeringmanagement.common.payload.SDto;
import com.digigate.engineeringmanagement.planning.constant.DefectType;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Defect  Dto
 *
 * @author Asifur Rahman
 */

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DefectDto implements SDto, IDto {

    private Long id;
    private Long defectId;
    @NotNull
    private Long aircraftId;
    private String aircraftName;
    private Long partId;
    private String partNo;
    private String nomenclature;
    private Long locationId;
    private String ata;
    @NotNull
    private LocalDate date;
    private String reference;
    private String defectDesc;
    private String actionDesc;
    private DefectType defectType;
}
