package com.digigate.engineeringmanagement.planning.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "forecasts_tasks", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"ldnd_id", "fa_id"})
})
public class ForecastTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ldnd_id", nullable = false)
    private Ldnd ldnd;

    @Column(name = "ldnd_id", nullable = false, insertable = false, updatable = false)
    private Long ldndId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fa_id", nullable = false)
    private ForecastAircraft forecastAircraft;

    @Column(name = "fa_id", nullable = false, insertable = false, updatable = false)
    private Long forecastAircraftId;

    @OneToMany(mappedBy = "forecastTask",
            cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true, fetch = FetchType.LAZY)
    Set<ForecastTaskPart> forecastTaskPartSet;

    @Column(nullable = false)
    private LocalDate dueDate;
    private String comment;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public void addForecastTaskPart(ForecastTaskPart forecastTaskPart) {
        if (Objects.isNull(forecastTaskPartSet)) {
            forecastTaskPartSet = new HashSet<>();
        }
        forecastTaskPartSet.add(forecastTaskPart);
        forecastTaskPart.setForecastTask(this);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof ForecastTask)) return false;
        return Objects.nonNull(this.getId()) && this.getId().equals(((ForecastTask) object).getId());
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }
}
