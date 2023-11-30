package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

import java.time.LocalDate;
import java.util.Set;

/**
 * task view model
 *
 * @author ashinisingha
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskViewModel {
    private Long taskId;
    private Long aircraftModelId;
    private String aircraftModelName;
    private Long modelId;
    private String modelName;
    private String taskNo;
    private String taskSource;
    private String taskTypeName;
    private Long taskTypeId;
    private Integer repeatType;
    private String description;
    private Double manHours;
    private String sources;
    private Integer status;

    private Integer intervalDay;
    private Double intervalHour;
    private Integer intervalCycle;
    private Integer thresholdDay;
    private Double thresholdHour;
    private Integer thresholdCycle;
    private Set<String> trade;
    private LocalDate effectiveDate;
    private Set<EffectiveAircraftViewModel> effectiveAircraftViewModels;
    private Set<TaskProcedureViewModel> taskProcedureViewModels;
    private Set<TaskConsumablePartViewModel> taskConsumablePartViewModels;
    private Boolean isApuControl;
    private Boolean isActive;
    private String comment;
    private String revisionNumber;
    private LocalDate issueDate;

    /**
     * Task Response payload for aircraft model
     *
     * @author Asifur Rahman
     */
    public TaskViewModel(Long taskId, String taskNo) {
        this.taskId = taskId;
        this.taskNo = taskNo;
    }

    /**
     * Task Response payload for aircraft model
     *
     * @author Asifur Rahman
     */
    public TaskViewModel(Long taskId, String taskNo, Long modelId) {
        this.taskId = taskId;
        this.taskNo = taskNo;
        this.modelId = modelId;
    }
}
