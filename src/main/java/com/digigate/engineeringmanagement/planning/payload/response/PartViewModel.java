package com.digigate.engineeringmanagement.planning.payload.response;

import com.digigate.engineeringmanagement.planning.constant.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Part view model
 *
 * @author ashinisingha
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PartViewModel {
    private Long id;
    private Long modelId;
    private String modelName;
    private String partNo;
    private String description;
    private Double countFactor;
    private PartClassification classification;
    private Long unitOfMeasureId;
    private String unitOfMeasureCode;
    private Long lifeLimit;
    private LifeLimitUnit lifeLimitUnit;
    private Long aircraftModelId;
    private String aircraftModelName;
    private ModelType modelType;
    @JsonProperty("alternateParts")
    Set<AlternatePartViewModel> alternatePartViewModelSet;
    List<PartWiseUomResponseDto> partWiseUomResponseDtoList;
    private Boolean isActive;
    @JsonIgnore
    private Long alternatePartId;

    public PartViewModel(Long id, Long modelId, String modelName, String partNo, String description,
                         PartClassification classification,Long unitOfMeasureId, String unitOfMeasureCode,
                         Boolean isActive) {
        this.id = id;
        this.modelId = modelId;
        this.modelName = modelName;
        this.partNo = partNo;
        this.description = description;
        this.classification = classification;
        this.unitOfMeasureId=unitOfMeasureId;
        this.unitOfMeasureCode = unitOfMeasureCode;
        this.isActive = isActive;
    }
    public PartViewModel(Long id, String partNo, Long alternatePartId) {
        this.id = id;
        this.partNo = partNo;
        this.alternatePartId = alternatePartId;
    }

    public PartViewModel(Long id, String partNo) {
        this.id = id;
        this.partNo = partNo;
    }

    public void addAlternatePart(AlternatePartViewModel part) {
        if(CollectionUtils.isEmpty(alternatePartViewModelSet)) {
            alternatePartViewModelSet = new HashSet<>();
        }
        if(!alternatePartViewModelSet.contains(part)) {
            alternatePartViewModelSet.add(part);
        }
    }
}
