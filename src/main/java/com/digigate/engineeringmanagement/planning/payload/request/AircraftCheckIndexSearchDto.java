package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.common.payload.SDto;
import lombok.*;

/**
 * AircraftCheckIndex SearchDto
 *
 * @author Ashraful
 */
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AircraftCheckIndexSearchDto implements SDto {

    private Boolean isActive;

    private Long aircraftId;

    private String woNo;
}
