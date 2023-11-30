package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.common.payload.SDto;
import lombok.*;

/**
 * Task search Dto
 *
 * @author ashinisingha
 */
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskSearchDto implements SDto {
    private Long aircraftModelId;
    private String taskNo;
    private String modelName;
    private Boolean isActive;
}
