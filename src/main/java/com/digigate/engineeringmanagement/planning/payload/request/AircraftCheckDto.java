package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.common.payload.IDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * AircraftCheck Dto
 *
 * @author Ashraful
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class AircraftCheckDto implements IDto {

    @NotNull
    private Long checkId;
    @NotNull
    private Long aircraftModelId;

    private Double flyingHour;

    private Long flyingDay;

    private Set<Long> taskIds;
}
