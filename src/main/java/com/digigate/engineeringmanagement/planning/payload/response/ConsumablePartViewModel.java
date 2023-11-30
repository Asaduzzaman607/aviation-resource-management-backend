package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

import javax.validation.constraints.NotNull;

/**
 * consumable part view model
 *
 * @author ashraful
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsumablePartViewModel {
    private Long id;
    private Long unitMeasurementId;
    private String unitMeasurementCode;
    private String partId;
    private String description;
    private Boolean isActive;
}
