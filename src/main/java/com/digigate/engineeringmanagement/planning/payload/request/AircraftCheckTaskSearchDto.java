package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.common.payload.SDto;
import lombok.*;

/**
 * AircraftCheckTask Search Dto
 *
 * @author Ashraful
 */
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AircraftCheckTaskSearchDto implements SDto {

    private Long acCheckId;
}
