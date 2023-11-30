package com.digigate.engineeringmanagement.planning.entity;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Aircraft;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * Aircraft cabin entity
 *
 * @author Pranoy Das
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "aircraft_cabins", uniqueConstraints={
        @UniqueConstraint(columnNames = {"aircraft_id","cabin_id"})
})
public class AircraftCabin extends AbstractDomainBasedEntity {

    @Column(nullable = false)
    private Integer noOfSeats;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cabin_id", nullable = false)
    private Cabin cabin;
    @Column(name = "cabin_id", insertable = false, updatable = false)
    private Long cabinId;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "aircraft_id", nullable = false)
    private Aircraft aircraft;
    @Column(name = "aircraft_id", insertable = false, updatable = false)
    private long aircraftId;
}
