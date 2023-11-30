package com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand;

import com.digigate.engineeringmanagement.common.payload.IDto;
import com.digigate.engineeringmanagement.storemanagement.constant.PriorityType;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Data
public class StoreIssueItemDto implements IDto {
    private Long id;
    private Long demandItemId;
    @Min(value = 0)
    @Max(value = 10000)
    private Integer issuedQuantity = 0;
    private Integer alreadyIssuedQuantity;
    @Size(max = 100)
    private String cardLineNo;
    private Set<GrnAndSerialDto> grnAndSerialDtoList = new HashSet<>();
    private String remark;
    private PriorityType priorityType;
    private Long uomId;
    private Long parentPartId;
}
