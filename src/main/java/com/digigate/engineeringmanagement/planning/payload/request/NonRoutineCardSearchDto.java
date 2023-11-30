package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.common.payload.SDto;
import lombok.*;

import javax.validation.constraints.NotNull;

/**
 * Non Routine Card Search Dto
 *
 * @author ashinisingha
 */
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NonRoutineCardSearchDto implements SDto {
    @NotNull
    private Long aircraftId;
    private Boolean isActive;
}
