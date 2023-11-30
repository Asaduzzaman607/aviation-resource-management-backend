package com.digigate.engineeringmanagement.planning.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Airport search dto class
 *
 * @author ashiniSingha
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AirportSearchDto {
    private String name;
    private String iataCode;
    private Boolean isActive;
}
