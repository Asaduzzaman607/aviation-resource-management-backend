package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.common.payload.SDto;
import lombok.*;

/**
 * AircraftCheck SearchDto
 *
 * @author Ashraful
 */
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AircraftCheckSearchDto implements SDto {

    private Boolean isActive;

    private Long aircraftModelId;
}
