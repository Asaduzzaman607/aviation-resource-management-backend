package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.planning.constant.EffectivityType;
import lombok.*;

import javax.validation.constraints.NotNull;

/**
 * Effective Aircraft Dto
 *
 * @author Pranoy Das
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EffectiveAircraftDto {
    private Long effectiveAircraftId;
    @NotNull
    private Long aircraftId;
    @NotNull
    private EffectivityType effectivityType;
    private String remark;
}
