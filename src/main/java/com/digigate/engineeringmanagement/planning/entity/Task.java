package com.digigate.engineeringmanagement.planning.entity;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.configurationmanagement.entity.AircraftModel;
import com.digigate.engineeringmanagement.planning.constant.RepetitiveTypeEnum;
import com.digigate.engineeringmanagement.planning.constant.TaskStatusEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Task entity
 *
 * @author ashinisingha
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "tasks")
public class Task extends AbstractDomainBasedEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aircraft_model_id")
    private AircraftModel aircraftModel;

    @Column(name = "aircraft_model_id", insertable = false, updatable = false)
    private Long aircraftModelId;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id")
    private Model model;

    @Column(name = "model_id", insertable = false, updatable = false)
    private Long modelId;

    @Column(nullable = false, unique = true)
    private String taskNo;

    @Column(nullable = false)
    private String taskSource;

    @Column(nullable = false, name = "repeat_type")
    private RepetitiveTypeEnum repetitiveType;

    @Column(length = 1000)
    private String description;

    private Double manHours;

    @Column(length = 500)
    private String sources;

    @Column(name = "status")
    private TaskStatusEnum taskStatus;

    private Boolean isApuControl;

    private Integer intervalDay;
    private Double intervalHour;
    private Integer intervalCycle;
    private Integer thresholdDay;
    private Double thresholdHour;
    private Integer thresholdCycle;

    private LocalDate effectiveDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "task_type_id")
    private TaskType taskType;

    @Column(name = "task_type_id", insertable = false, updatable = false)
    private Long taskTypeId;

    @ElementCollection
    @Column(nullable = false)
    private Set<String> trade;

    @Column(length = 500)
    private String comment;

    private String revisionNumber;

    private LocalDate issueDate;

    @JsonIgnore
    @OneToMany(mappedBy = "task",
            cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
    private Set<AircraftEffectivity> aircraftEffectivitySet;

    @JsonIgnore
    @OneToMany(mappedBy = "task",
            cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<TaskProcedure> taskProcedureSet;

    @JsonIgnore
    @OneToMany(mappedBy = "task",
            cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<TaskConsumablePart> taskConsumablePartSet;


    public void addEffectiveAircraft(AircraftEffectivity aircraftEffectivity) {
        if (Objects.isNull(aircraftEffectivitySet)) {
            aircraftEffectivitySet = new HashSet<>();
        }

        aircraftEffectivitySet.add(aircraftEffectivity);
        aircraftEffectivity.setTask(this);
    }

    public void addTaskProcedure(TaskProcedure taskProcedure) {
        if (Objects.isNull(taskProcedureSet)) {
            taskProcedureSet = new HashSet<>();
        }

        taskProcedureSet.add(taskProcedure);
        taskProcedure.setTask(this);
    }

    public void addTaskConsumablePart(TaskConsumablePart taskConsumablePart) {
        if (Objects.isNull(taskConsumablePartSet)) {
            taskConsumablePartSet = new HashSet<>();
        }
        taskConsumablePartSet.add(taskConsumablePart);
        taskConsumablePart.setTask(this);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Task)) return false;
        return Objects.nonNull(this.getId()) && this.getId().equals(((Task) object).getId());
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }
}

