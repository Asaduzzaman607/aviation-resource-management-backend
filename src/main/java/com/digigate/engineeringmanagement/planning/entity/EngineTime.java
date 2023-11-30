package com.digigate.engineeringmanagement.planning.entity;

import com.digigate.engineeringmanagement.planning.constant.ModelType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "engine_times")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EngineTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aircraft_build_id", nullable = false)
    private AircraftBuild aircraftBuild;

    @Column(name = "aircraft_build_id", insertable = false, updatable = false)
    private Long aircraftBuildId;

    @Column(nullable = false)
    private String nameExtension;

    @Column(nullable = false)
    private ModelType type;

    private LocalDate date;

    private Double hour;

    private Integer cycle;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
