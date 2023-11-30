package com.digigate.engineeringmanagement.planning.entity;


import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.configurationmanagement.entity.AircraftModel;
import com.digigate.engineeringmanagement.planning.constant.ModelType;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "models")
public class Model extends AbstractDomainBasedEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private AircraftModel aircraftModel;

    @Column(name = "aircraft_model_id", insertable = false, updatable = false)
    private Long aircraftModelId;

    @Column(nullable = false, unique = true)
    private String modelName;

    private ModelType modelType;

    private String description;

    @ElementCollection
    private Set<Integer> lifeCodes;

    private String version;
}
