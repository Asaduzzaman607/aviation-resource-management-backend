package com.digigate.engineeringmanagement.planning.payload.request;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Aircraft Build part and serial search dto
 *
 * @author ashinisingha
 */

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AircraftBuildPartSerialSearchDto {
    @NotNull
    private Long partId;

    @NotNull
    private Long serialId;
}
