package com.digigate.engineeringmanagement.procurementmanagement.dto.response;

import com.digigate.engineeringmanagement.planning.payload.response.PartWiseUomResponseDto;
import com.digigate.engineeringmanagement.storemanagement.constant.PriorityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RfqPartViewModel {
    private Long id;
    private Long demandItemId;
    private Long iqItemId;
    private Long partId;
    private String partNo;
    private String partDescription;
    private Long unitMeasurementId;
    private String unitMeasurementCode;
    private Integer quantityRequested;
    private PriorityType priority;
}
