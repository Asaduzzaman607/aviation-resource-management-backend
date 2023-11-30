package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.common.payload.SDto;
import lombok.*;

import javax.validation.constraints.NotNull;

/**
 * consumable part search Dto
 *
 * @author ashinisingha
 */
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConsumablePartSearchDto implements SDto {
    private Long unitMeasurementId;
    private String partId;
    private Boolean isActive;
}
