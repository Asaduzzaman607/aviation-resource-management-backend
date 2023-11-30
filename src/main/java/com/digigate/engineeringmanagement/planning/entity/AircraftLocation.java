package com.digigate.engineeringmanagement.planning.entity;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * aircraft location entity
 *
 * @author ashiniSingha
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "aircraft_locations")
public class AircraftLocation extends AbstractDomainBasedEntity {
    @Column(nullable = false, unique = true)
    private String name;
    private String description;
    private String remarks;
}
