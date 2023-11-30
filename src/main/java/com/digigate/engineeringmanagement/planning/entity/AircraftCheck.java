package com.digigate.engineeringmanagement.planning.entity;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.configurationmanagement.entity.AircraftModel;
import lombok.*;
import javax.persistence.*;
import java.util.Set;

/**
 * AircraftCheck Entity
 *
 * @author Ashraful
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "ac_type_checks", uniqueConstraints={
        @UniqueConstraint(columnNames = {"check_id", "aircraft_model_id"})
})
public class AircraftCheck extends AbstractDomainBasedEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "check_id")
    private Check check;

    @Column(name = "check_id", insertable = false, updatable = false, nullable = false)
    private Long checkId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aircraft_model_id")
    private AircraftModel aircraftModel;

    @Column(name = "aircraft_model_id", insertable = false, updatable = false, nullable = false)
    private Long aircraftModelId;

    private Double flyingHour;

    private Long flyingDay;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(
            name = "ac_type_check_tasks",
            joinColumns = {@JoinColumn(name = "ac_check_id")},
            inverseJoinColumns = {@JoinColumn(name = "task_id")}
    )
    private Set<Task> aircraftCheckTasks;
}

