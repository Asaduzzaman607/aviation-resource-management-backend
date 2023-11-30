package com.digigate.engineeringmanagement.planning.entity;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.common.entity.erpDataSync.Employee;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Aircraft;
import com.digigate.engineeringmanagement.planning.constant.AmlType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Aircraft maintenance log entity
 *
 * @author Pranoy Das
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "amls", uniqueConstraints={
        @UniqueConstraint(columnNames = {"page_no","alphabet", "aircraft_id"})
})
public class AircraftMaintenanceLog extends AbstractDomainBasedEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aircraft_id")
    private Aircraft aircraft;

    @Column(name = "aircraft_id", insertable = false, updatable = false)
    private Long amlAircraftId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_airport")
    private Airport fromAirport;

    @Column(name = "from_airport", insertable = false, updatable = false)
    private Long fromAirportId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_airport")
    private Airport toAirport;

    @Column(name = "to_airport", insertable = false, updatable = false)
    private Long toAirportId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pfi_station_id")
    private Airport preFlightInspectionAirport;

    @Column(name = "pfi_station_id", insertable = false, updatable = false)
    private Long preFlightInspectionAirportId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "captain_id")
    private Employee captain;

    @Column(name = "captain_id", insertable = false, updatable = false)
    private Long captainId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "of_id")
    private Employee firstOfficer;

    @Column(name = "of_id", insertable = false, updatable = false)
    private Long firstOfficerId;

    private LocalDateTime pfiTime;

    private LocalDateTime ocaTime;

    @Column(nullable = false, name = "page_no")
    private Integer pageNo;

    @Column(name = "alphabet")
    private Character alphabet;

    private String flightNo;

    @Column(nullable = false)
    private LocalDate date;

    private Double refuelDelivery;

    private Double specificGravity;

    private Double convertedIn;

    private String remarks;

    private AmlType amlType;

    @OneToMany(mappedBy = "aircraftMaintenanceLog",
            cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    Set<AircraftMaintenanceLogSignature> aircraftMaintenanceLogSignatures;

    @JsonIgnore
    @OneToOne(mappedBy = "aircraftMaintenanceLog",
            cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true, fetch = FetchType.LAZY)
    private AmlFlightData flightData;

    @JsonIgnore
    @OneToMany(mappedBy = "aircraftMaintenanceLog",
            cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true, fetch = FetchType.LAZY)
    Set<AmlOilRecord> amlOilRecords;

    @JsonIgnore
    @OneToMany(mappedBy = "aircraftMaintenanceLog",
            cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true, fetch = FetchType.LAZY)
    Set<AMLDefectRectification> amlDefectRectifications;

    public void addAircraftMaintenanceLogSignature(AircraftMaintenanceLogSignature aircraftMaintenanceLogSignature) {
        if(Objects.isNull(aircraftMaintenanceLogSignatures)) {
            aircraftMaintenanceLogSignatures = new HashSet<>();
        }
        aircraftMaintenanceLogSignatures.add(aircraftMaintenanceLogSignature);
        aircraftMaintenanceLogSignature.setAircraftMaintenanceLog(this);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof AircraftMaintenanceLog)) return false;
        return Objects.nonNull(this.getId()) && this.getId().equals(((AircraftMaintenanceLog) object).getId());
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }
}

