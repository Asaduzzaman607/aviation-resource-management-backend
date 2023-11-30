package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.planning.constant.TaskStatusEnum;
import lombok.*;

/**
 * Dto For Save Last Done Task By Aircraft
 *
 * @author  Sayem Hasnat
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskDoneSaveDto {
    private Long taskId;
    private Long modelId;
    private String taskNo;
    private Boolean isApuControl;
    private Integer repetitiveType;
    private TaskStatusEnum taskStatus;
}
