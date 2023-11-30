package com.digigate.engineeringmanagement.planning.dto.request;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * Airport Dto class
 *
 * @author ashiniSingha
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AirportDto {
    @NotBlank(message = ErrorId.AIRPORT_NAME_MUST_NOT_BE_EMPTY)
    private String name;
    @NotBlank(message = ErrorId.IATA_CODE_MUST_NOT_BE_EMPTY)
    private String iataCode;
    private String countryCode;
    private Boolean isActive;
}
