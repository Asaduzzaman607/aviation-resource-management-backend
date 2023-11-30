package com.digigate.engineeringmanagement.planning.entity;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.configurationmanagement.constant.CancellationTypeEnum;
import com.digigate.engineeringmanagement.configurationmanagement.entity.AircraftModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDate;

/**
 * AcCancellations Entity
 *
 * @author Nafiul Islam
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ac_cancellatios")
public class AcCancellations extends AbstractDomainBasedEntity {

    @ManyToOne
    private AircraftModel aircraftModel;

    @Column(name = "aircraft_model_id", insertable = false, updatable = false, nullable = false)
    private Long aircraftModelId;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "cancellation_type", nullable = false)
    private CancellationTypeEnum cancellationTypeEnum;

}
