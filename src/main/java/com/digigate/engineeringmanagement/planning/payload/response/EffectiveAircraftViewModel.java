package com.digigate.engineeringmanagement.planning.payload.response;

import com.digigate.engineeringmanagement.planning.constant.EffectivityType;
import lombok.*;

/**
 * Effective Aircraft View Model
 *
 * @author Pranoy Das
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EffectiveAircraftViewModel {
    private Long effectiveAircraftId;
    private Long aircraftId;
    private String aircraftName;
    private EffectivityType effectivityType;
    private String remark;
}
