package com.digigate.engineeringmanagement.planning.payload.response;

import com.digigate.engineeringmanagement.planning.constant.PartClassification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SerialResponseDto {
    private Long id;
    private Long partId;
    private String serialNumber;
    private String partNo;
    private Long modelId;
    private String modelName;
    private Long aircraftModelId;
    private PartClassification classification;
    private Boolean isActive;
}
