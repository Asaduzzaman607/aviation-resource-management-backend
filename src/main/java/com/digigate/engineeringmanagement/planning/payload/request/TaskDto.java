package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.common.payload.IDto;
import com.digigate.engineeringmanagement.planning.constant.RepetitiveTypeEnum;
import com.digigate.engineeringmanagement.planning.constant.TaskStatusEnum;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * task dto
 *
 * @author Pranoy Das
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDto implements IDto {
    @NotNull
    private Long aircraftModelId;
    @NotNull
    private Long modelId;
    private String taskNo;
    @NotBlank
    private String taskSource;
    @NotNull
    private RepetitiveTypeEnum repeatType;
    private String description;
    private Double manHours;
    private String sources;
    private TaskStatusEnum status;
    private Boolean isApuControl;
    private Integer intervalDay;
    private Double intervalHour;
    private Integer intervalCycle;
    private Integer thresholdDay;
    private Double thresholdHour;
    private Integer thresholdCycle;
    private Long taskTypeId;
    @NotEmpty
    private Set<String> trade;
    private LocalDate effectiveDate;
    private List<EffectiveAircraftDto> effectiveAircraftDtoList;
    private List<TaskProcedureDto> taskProcedureDtoList;
    private List<TaskConsumablePartDto> taskConsumablePartDtoList;
    private String comment;
    private String revisionNumber;
    private LocalDate issueDate;
}
