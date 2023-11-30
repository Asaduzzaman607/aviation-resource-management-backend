package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.common.payload.IDto;
import com.digigate.engineeringmanagement.planning.constant.EngineIncidentsEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Engine Incidents  Dto
 *
 * @author Nafiul Islam
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EngineIncidentsDto implements IDto {

    @NotNull
    private Long aircraftModelId;

    @NotNull
    private EngineIncidentsEnum engineIncidentsEnum;

    @NotNull
    private LocalDate date;
}
