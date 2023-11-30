package com.digigate.engineeringmanagement.planning.entity;


import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Aircraft;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Objects;

/**
 * AircraftBuild Entity
 *
 * @author Masud Rana
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "aircraft_builds")
public class AircraftBuild extends AbstractDomainBasedEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aircraft_id")
    private Aircraft aircraft;
    @Column(name = "aircraft_id", insertable = false, updatable = false, nullable = false)
    private Long aircraftId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "higher_model_id")
    private Model higherModel;
    @Column(name = "higher_model_id", insertable = false, updatable = false, nullable = false)
    private Long higherModelId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id")
    private Model model;
    @Column(name = "model_id", insertable = false, updatable = false, nullable = false)
    private Long modelId;

    private Double tsnHour;
    private Integer tsnCycle;
    private Boolean isTsnAvailable = true;

    private Double tsoHour = 0.0;
    private Integer tsoCycle = 0;
    private Boolean isOverhauled = false;
    private Double tslsvHour = 0.0;
    private Integer tslsvCycle = 0;
    private Boolean isShopVisited = false;

    @Column(nullable = false)
    private LocalDate attachDate;
    private LocalDate comManufactureDate;
    private LocalDate comCertificateDate;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id")
    private Position position;

    @Column(name = "position_id", insertable = false, updatable = false)
    private Long positionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private AircraftLocation aircraftLocation;

    @Column(name = "location_id", insertable = false, updatable = false, nullable = false)
    private Long locationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_id")
    private Part part;
    @Column(name = "part_id", insertable = false, updatable = false, nullable = false)
    private Long partId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "serial_id", nullable = false)
    private Serial serial;

    @Column(name = "serial_id", insertable = false, updatable = false, nullable = false)
    private Long serialId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "higher_part_id")
    private Part higherPart;
    @Column(name = "higher_part_id", insertable = false, updatable = false, nullable = false)
    private Long higherPartId;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "higher_serial_id", nullable = false)
    private Serial higherSerial;

    @Column(name = "higher_serial_id", insertable = false, updatable = false, nullable = false)
    private Long higherSerialId;

    @Column(nullable = false, columnDefinition ="float default 0.0" )
    private Double aircraftInHour;

    @Column(nullable = false, columnDefinition ="int default 0" )
    private Integer aircraftInCycle;

    private LocalDate outDate;

    private String inRefMessage;

    private String outRefMessage;

    private String removalReason;

    private Double aircraftOutHour;

    private Integer aircraftOutCycle;
    @Size(max = 8000)
    private String authNo;
    @Size(max = 8000)
    private String sign;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof AircraftBuild)) return false;
        return Objects.nonNull(this.getId()) && this.getId().equals(((AircraftBuild) object).getId());
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }
}
