package com.digigate.engineeringmanagement.common.payload.response.erpDataSyncResponseModel;
import com.digigate.engineeringmanagement.common.payload.request.erp.DesignationDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class DesignationModelDto implements Serializable {
    @JsonProperty("model")
    private List<DesignationDto> erpDesignations;
    @JsonProperty("totalPages")
    private Long totalPages;
    @JsonProperty("currentPage")
    private Long currentPage;
    @JsonProperty("totalElements")
    private Long totalElements;
}
