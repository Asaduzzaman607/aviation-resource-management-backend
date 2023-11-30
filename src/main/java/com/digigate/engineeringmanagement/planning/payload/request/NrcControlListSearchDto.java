package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.common.payload.SDto;
import lombok.*;

import javax.validation.constraints.NotNull;

/**
 * Nrc ControlList Search Dto
 *
 * @author ashinisingha
 */

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NrcControlListSearchDto implements SDto {
    @NotNull
    private Long aircraftId;
}
