package com.digigate.engineeringmanagement.planning.entity;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.common.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

/**
 * Cabin entity class
 *
 * @author Pranoy Das
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@Entity
@Table(name = "cabins")
public class Cabin extends AbstractDomainBasedEntity {
    @Column(nullable = false, unique = true)
    private Character code;

    @Column(nullable = false, unique = true)
    private String title;

    private Boolean isActive = true;

    @JsonIgnore
    @OneToMany(mappedBy = "cabin", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
    private Set<AircraftCabin> aircraftCabinSet;
}
