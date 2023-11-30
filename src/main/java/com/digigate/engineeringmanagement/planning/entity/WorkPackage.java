package com.digigate.engineeringmanagement.planning.entity;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Aircraft;
import com.digigate.engineeringmanagement.planning.constant.PackageType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Work Package entity
 *
 * @author ashinisingha
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "work_packages")
public class WorkPackage extends AbstractDomainBasedEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ac_check_index_id", nullable = false)
    private AircraftCheckIndex aircraftCheckIndex;
    @Column(name = "ac_check_index_id", insertable = false, updatable = false)
    private Long acCheckIndexId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aircraft_id", nullable = false)
    private Aircraft aircraft;
    @Column(name = "aircraft_id", insertable = false, updatable = false)
    private Long aircraftId;

    @Column(nullable = false)
    private PackageType packageType;

    private LocalDate inputDate;
    private LocalDate releaseDate;
    @Column(nullable = false)
    private Double acHours;
    @Column(nullable = false)
    private Integer acCycle;

    @OneToMany(mappedBy = "workPackage", orphanRemoval = true )
    private List<JobCards> jobCardsList;

    private LocalDate asOfDate;
}
