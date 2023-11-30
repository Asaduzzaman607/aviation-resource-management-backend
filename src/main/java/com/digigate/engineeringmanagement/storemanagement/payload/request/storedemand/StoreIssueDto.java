package com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand;

import com.digigate.engineeringmanagement.common.payload.IDto;
import com.digigate.engineeringmanagement.planning.constant.PartClassification;
import com.digigate.engineeringmanagement.storemanagement.constant.StockRoomType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoreIssueDto implements IDto {
    @NotNull
    private Long demandId;
    private StockRoomType stockRoomType = StockRoomType.ISSUE_DEMAND_CONSUMABLE;
    private PartClassification partClassification = PartClassification.ROTABLE;
    private Long storeStockRoomId;
    @Size(max = 8000)
    private String registration;
    private LocalDateTime approvedOn;
    @Size(max = 8000)
    private String remarks;
    @NotEmpty
    @Valid
    private List<StoreIssueItemDto> storeIssueItems;
}
