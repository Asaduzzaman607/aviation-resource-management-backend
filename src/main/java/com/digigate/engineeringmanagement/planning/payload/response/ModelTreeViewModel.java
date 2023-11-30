package com.digigate.engineeringmanagement.planning.payload.response;


import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelTreeViewModel {
    private Long id;
    private Long locationId;
    private Long aircraftModelId;
    private String locationName;
    private Long modelId;
    private String modelName;
    private Long higherModelId;
    private String higherModelName;
    private Long positionId;
    private String positionName;
    private LocalDateTime createdAt;
    private Boolean isActive;

    public ModelTreeViewModel(Long modelId, String modelName) {
        this.modelId = modelId;
        this.modelName = modelName;
    }

    public ModelTreeViewModel(Long locationId, String locationName, Long positionId, String positionName) {
        this.locationId = locationId;
        this.locationName = locationName;
        this.positionId = positionId;
        this.positionName = positionName;
    }

    public ModelTreeViewModel(Long id, Long locationId, Long modelId, Long higherModelId, Long positionId) {
        this.id = id;
        this.locationId = locationId;
        this.modelId = modelId;
        this.higherModelId = higherModelId;
        this.positionId = positionId;
    }
}
