package com.digigate.engineeringmanagement.common.payload.request.erp;
import com.digigate.engineeringmanagement.common.payload.IDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class DepartmentSyncDto implements IDto, Serializable {
    @JsonProperty("company_id")
    private Long companyId;
    private List<DepartmentDataDto> departments;
}
