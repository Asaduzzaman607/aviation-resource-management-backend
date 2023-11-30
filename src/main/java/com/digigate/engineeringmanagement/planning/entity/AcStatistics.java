package com.digigate.engineeringmanagement.planning.entity;

import com.digigate.engineeringmanagement.configurationmanagement.entity.AircraftModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ac_statistics")
public class AcStatistics  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private AircraftModel aircraftModel;

    @Column(name = "aircraft_model_id", insertable = false, updatable = false, nullable = false)
    private Long aircraftModelId;

    @Column(nullable = false)
    private Long totalServiceDay;

    @Column(nullable = false)
    private Double totalFlightHour;

    @Column(nullable = false)
    private Integer totalFlightCycle;

    @Column(nullable = false)
    private Integer month;

    @Column(nullable = false)
    private Integer year;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
