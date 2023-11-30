package com.digigate.engineeringmanagement.planning.entity;

import com.digigate.engineeringmanagement.planning.constant.ModelType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "engine_shop_visits")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EngineShopVisit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aircraft_build_id", nullable = false)
    private AircraftBuild aircraftBuild;

    @Column(name = "aircraft_build_id", insertable = false, updatable = false)
    private Long aircraftBuildId;

    @Column(nullable = false)
    private ModelType type;

    private LocalDate date;

    private Double tsn;

    private Integer csn;

    private Double tso;

    private Integer cso;

    private String status;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
