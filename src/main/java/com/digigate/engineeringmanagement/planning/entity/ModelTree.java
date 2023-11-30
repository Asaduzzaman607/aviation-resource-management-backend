package com.digigate.engineeringmanagement.planning.entity;


import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "model_trees", uniqueConstraints={
        @UniqueConstraint(columnNames = {"location_id", "model_id", "higher_model_id", "position_id"})
})
public class ModelTree extends AbstractDomainBasedEntity {

    @ManyToOne
    @JoinColumn(name = "location_id")
    private AircraftLocation aircraftLocation;

    @Column(name = "location_id", insertable = false, updatable = false)
    private Long locationId;

    @ManyToOne
    @JoinColumn(name = "model_id")
    private Model model;

    @Column(name = "model_id", insertable = false, updatable = false)
    private Long modelId;

    @ManyToOne
    @JoinColumn(name = "higher_model_id")
    private Model higherModel;

    @Column(name = "higher_model_id", insertable = false, updatable = false)
    private Long higherModelId;

    @ManyToOne
    @JoinColumn(name = "position_id")
    private Position position;

    @Column(name = "position_id", insertable = false, updatable = false)
    private Long positionId;


    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof ModelTree)) return false;
        return Objects.nonNull(this.getId()) && this.getId().equals(((ModelTree) object).getId());
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }
}
