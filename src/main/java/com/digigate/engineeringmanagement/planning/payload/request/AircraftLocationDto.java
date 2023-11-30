package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.common.payload.IDto;
import lombok.*;

import javax.validation.constraints.NotBlank;

/**
 * Aircraft location dto
 *
 * @author ashiniSingha
 */

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class AircraftLocationDto implements IDto {
    private Long id;
    @NotBlank
    private String name;
    private String description;
    private String remarks;
    private Boolean isActive;
}
