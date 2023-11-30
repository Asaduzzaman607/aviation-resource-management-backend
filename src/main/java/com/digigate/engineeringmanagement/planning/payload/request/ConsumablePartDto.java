package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.common.payload.IDto;
import lombok.*;

import javax.validation.constraints.NotNull;

/**
 * consumable part dto
 *
 * @author ashraful
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsumablePartDto implements IDto {
    @NotNull
    private Long unitMeasurementId;
    private String partId;
    private String description;
}
