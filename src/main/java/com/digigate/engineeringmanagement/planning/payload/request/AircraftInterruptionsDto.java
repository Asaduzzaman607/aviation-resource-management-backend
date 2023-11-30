package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.common.payload.IDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Aircraft Interruptions Dto
 *
 * @author Nafiul Islam
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AircraftInterruptionsDto implements IDto {
    @NotNull
    private Long aircraftId;
    @NotNull
    private Long locationId;
    @NotNull
    private LocalDate date;
    @NotNull
    private String amlPageNo;
    @NotNull
    private String seqNo;
    private String defectDescription;
    private String rectDescription;
    private Double duration;
}
