package com.digigate.engineeringmanagement.planning.dto.request;

import com.digigate.engineeringmanagement.configurationmanagement.entity.Aircraft;
import com.digigate.engineeringmanagement.planning.entity.Task;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Aircraft Effectivity Dto
 *
 * @author Sayem Hasnat
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AircraftEffectivityDto {
    private Task task;
    private Aircraft aircraft;
    private Integer effectivityType;

    public AircraftEffectivityDto(Task task) {
        this.task = task;
    }
}