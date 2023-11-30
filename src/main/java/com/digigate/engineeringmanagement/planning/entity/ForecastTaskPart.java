package com.digigate.engineeringmanagement.planning.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "ft_parts")
public class ForecastTaskPart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_id")
    private Part part;

    @Column(name = "part_id", insertable = false, updatable = false)
    private Long partId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "f_task_id", nullable = false)
    private ForecastTask forecastTask;

    @Column(name = "f_task_id", insertable = false, updatable = false, nullable = false)
    private Long forecastTaskId;

    @Column(nullable = false)
    private Long quantity;
    private String ipcRef;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof ForecastTaskPart)) return false;
        return Objects.nonNull(this.getId()) && this.getId().equals(((ForecastTaskPart) object).getId());
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }
}
