package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.common.payload.SDto;
import lombok.*;

/**
 * Engine model search payload
 *
 * @author Pranoy Das
 */
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EngineModelSearchDto implements SDto {
    private Long engineModelTypeId;
    private String position;
    private Boolean isActive = true;
}
