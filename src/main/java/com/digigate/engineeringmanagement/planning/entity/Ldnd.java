package com.digigate.engineeringmanagement.planning.entity;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Aircraft;
import com.digigate.engineeringmanagement.planning.constant.IntervalType;
import com.digigate.engineeringmanagement.planning.constant.TaskStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Ldnd  Entity
 *
 * @author Asifur Rahman
 */

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ldnd")
public class Ldnd extends AbstractDomainBasedEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @Column(name = "task_id", insertable = false, updatable = false)
    private Long taskId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aircraft_id", nullable = false)
    private Aircraft aircraft;

    @Column(name = "aircraft_id", insertable = false, updatable = false)
    private Long aircraftId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_procedure_id")
    private TaskProcedure taskProcedure;

    @Column(name = "task_procedure_id", insertable = false, updatable = false)
    private Long taskProcedureId;

    @ManyToOne
    @JoinColumn(name = "part_id", nullable = false)
    private Part part;

    @Column(name = "part_id", insertable = false, updatable = false)
    private Long partId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "serial_id", nullable = false)
    private Serial serial;

    @Column(name = "serial_id", insertable = false, updatable = false)
    private Long serialId;


    private LocalDate doneDate;

    @Column(nullable = false)
    private Boolean isApuControl;

    private Double doneHour;
    private Integer doneCycle;

    private LocalDate dueDate;
    private Double dueHour;
    private Integer dueCycle;

    @Transient
    private Long remainingDay;
    private Double remainingHour;
    private Integer remainingCycle;
    private LocalDate estimatedDueDate;

    private Double initialHour;
    private Integer initialCycle;
    private IntervalType intervalType;

    private Integer noOfMan;
    private Double elapsedTime;
    @Column(name = "actual_mh")
    private Double actualManHour;

    private TaskStatusEnum taskStatus;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Ldnd)) return false;
        return Objects.nonNull(this.getId()) && this.getId().equals(((Ldnd) object).getId());
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }
}
