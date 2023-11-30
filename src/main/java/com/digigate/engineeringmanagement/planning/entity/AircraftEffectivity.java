package com.digigate.engineeringmanagement.planning.entity;

import com.digigate.engineeringmanagement.configurationmanagement.entity.Aircraft;
import com.digigate.engineeringmanagement.planning.constant.EffectivityType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Effective Aircraft Entity
 *
 * @author Pranoy Das
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "aircraft_effectivities")
public class AircraftEffectivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Task task;

    @Column(name = "task_id", insertable = false, updatable = false)
    private Long taskId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aircraft_id")
    private Aircraft aircraft;

    @Column(name = "aircraft_id", insertable = false, updatable = false)
    private Long aircraftId;

    private String remark;

    @Column(nullable = false)
    private EffectivityType effectivityType;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof AircraftEffectivity)) return false;
        return Objects.nonNull(this.getId()) && this.getId().equals(((AircraftEffectivity) object).getId());
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }
}
