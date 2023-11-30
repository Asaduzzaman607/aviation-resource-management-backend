package com.digigate.engineeringmanagement.configurationmanagement.dto.request.aircraftinformation;

import com.digigate.engineeringmanagement.common.payload.IDto;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AircraftModelDto implements IDto {
    @NotBlank
    @Length(max = 50)
    private String aircraftModelName;
    private String description;
    private Long id;
    private Double checkHourForA;
    private Integer checkDaysForA;
    private Double checkHourForC;
    private Integer checkDaysForC;
}
