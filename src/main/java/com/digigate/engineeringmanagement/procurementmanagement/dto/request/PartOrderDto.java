package com.digigate.engineeringmanagement.procurementmanagement.dto.request;

import com.digigate.engineeringmanagement.common.payload.IDto;
import lombok.Data;

import java.util.List;

@Data
public class PartOrderDto implements IDto {
    private Long csDetailId;
    private List<Long> itemIdList;
}
