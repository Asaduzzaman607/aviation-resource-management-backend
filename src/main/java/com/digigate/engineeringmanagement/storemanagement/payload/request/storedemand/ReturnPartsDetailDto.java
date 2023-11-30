package com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand;

import com.digigate.engineeringmanagement.common.payload.IDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReturnPartsDetailDto implements IDto {
    private Long id;
    private Long storeReturnPartId;
    @JsonProperty("installedPartSerialId")
    private Long installedPlanningSerialId;
    @JsonProperty("removedPartSerialId")
    private Long removedPlanningSerialId;
    private Long positionId;
    private Long aircraftId;
    private Long airportId;
    @Size(max = 100)
    private String authCodeNo;
    @Size(max = 8000)
    private String reasonRemoved;
    private LocalDate removalDate;
    private Double tsn;
    private Integer csn;
    private Double tsr;
    private Integer csr;
    private Double tso;
    private Integer cso;
    private Boolean isUsed = Boolean.FALSE;
    private String authNo;
    private String sign;
    private LocalDate createdDate;
}
