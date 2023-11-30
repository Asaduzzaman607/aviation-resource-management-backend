package com.digigate.engineeringmanagement.planning.entity;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.configurationmanagement.entity.AircraftModel;
import com.digigate.engineeringmanagement.planning.constant.LifeLimitUnit;
import com.digigate.engineeringmanagement.planning.constant.PartClassification;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.UnitMeasurement;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * Part entity class
 *
 * @author ashinisingha
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "parts", uniqueConstraints={
        @UniqueConstraint(columnNames = {"model_id","part_no"})
})
public class Part extends AbstractDomainBasedEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id")
    private Model model;

    @Column(name = "model_id", insertable = false, updatable = false)
    private Long modelId;

    @Column(name = "part_no", nullable = false)
    private String partNo;

    private String description;

    private Double countFactor;

    @Enumerated
    private PartClassification classification;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", nullable = false)
    private UnitMeasurement unitMeasurement;

    @Column(name = "unit_id", insertable = false, updatable = false)
    private Long unitMeasurementId;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(
            name = "alternate_parts",
            joinColumns = {@JoinColumn(name = "part_id")},
            inverseJoinColumns = {@JoinColumn(name = "alternate_part_id")}
    )
    private Set<Part> alternatePartSet;

    private Long lifeLimit;

    private LifeLimitUnit lifeLimitUnit;

    public static Part withId(Long partId) {
        Part part = new Part();
        part.setId(partId);
        return part;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Part)) return false;
        return Objects.nonNull(this.getId()) && this.getId().equals(((Part) object).getId());
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }
}
