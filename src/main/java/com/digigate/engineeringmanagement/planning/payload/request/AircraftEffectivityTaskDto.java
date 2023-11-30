
package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.planning.constant.EffectivityType;
import lombok.*;

/**
 * Aircraft Effectivity request dto
 *
 * @author Sayem Hasnat
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AircraftEffectivityTaskDto {
    private Long effectivityId;
    @NonNull
    private Long aircraftId;
    @NonNull
    private Long taskId;
    private String taskName;
    private String remark;
    @NonNull
    private EffectivityType effectivityType;

    public AircraftEffectivityTaskDto(Long taskId, String taskName) {
        this.taskId = taskId;
        this.taskName = taskName;
    }
}