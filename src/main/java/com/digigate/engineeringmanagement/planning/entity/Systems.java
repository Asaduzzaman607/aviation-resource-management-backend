package com.digigate.engineeringmanagement.planning.entity;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import lombok.*;

import javax.persistence.*;

/**
 * Systems  Entity
 *
 * @author Nafiul Islam
 */

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "systems")
public class Systems extends AbstractDomainBasedEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false, unique = true)
    private AircraftLocation aircraftLocation;

    @Column(name = "location_id", insertable = false, updatable = false)
    private Long locationId;

    @Column(nullable = false)
    private String name;
}
