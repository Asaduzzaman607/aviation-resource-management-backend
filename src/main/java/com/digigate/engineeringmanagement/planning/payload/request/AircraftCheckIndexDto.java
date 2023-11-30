package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.common.payload.IDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Set;

/**
 * AircraftCheckIndex Dto
 *
 * @author Ashraful
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class AircraftCheckIndexDto implements IDto {

    @NotNull
    private Long aircraftId;

    private Long woId;

    private Date doneDate;

    private Double doneHour;

    private Integer doneCycle;

    private Set<Long> aircraftTypeCheckIds;

    private Set<Long> ldndIds;
}
