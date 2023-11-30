package com.digigate.engineeringmanagement.planning.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Serial Part SearchDto
 *
 * @author Nafiul Islam
 */

@Getter
@Setter
@AllArgsConstructor
public class SerialPartSearchDto {
    private String serialNumber;
    private Long partId;
    private Boolean isActive;
    private String partNo;
}
