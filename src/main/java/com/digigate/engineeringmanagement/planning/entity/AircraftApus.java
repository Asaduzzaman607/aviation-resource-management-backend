package com.digigate.engineeringmanagement.planning.entity;

import com.digigate.engineeringmanagement.configurationmanagement.entity.Aircraft;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * AircraftApus Entity
 *
 * @author Nafiul Islam
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "aircraft_apus")
public class AircraftApus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aircraft_id", nullable = false, unique = true)
    private Aircraft aircraft;

    @Column(name = "aircraft_id", insertable = false, updatable = false)
    private Long aircraftId;

    private String model;
    private LocalDate date;
    private Double tsn;
    private Integer csn;
    private Double tsr;
    private Integer csr;
    private String status;
    @JsonIgnore
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
