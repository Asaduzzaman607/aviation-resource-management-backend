package com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand;

import com.digigate.engineeringmanagement.common.payload.IDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProcurementRequisitionDto implements IDto {
    private Long id;
    @NotNull
    private Long storeDemandId;
    @Size(max = 8000)
    private String remarks;

    private Set<String> attachment;
    @NotEmpty
    @Valid
    private List<ProcurementRequisitionItemDto> procurementRequisitionItemDtoList;
}
