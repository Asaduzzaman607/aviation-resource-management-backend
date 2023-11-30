package com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand;

import com.digigate.engineeringmanagement.common.payload.IDto;
import com.digigate.engineeringmanagement.planning.constant.PartClassification;
import com.digigate.engineeringmanagement.storemanagement.constant.StockRoomType;
import com.digigate.engineeringmanagement.storemanagement.constant.StoreReturnStatusType;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StoreReturnRequestDto implements IDto {
    private Long id;
    private Boolean isInternalDept = true;
    @Size(max = 100)
    private String aircraftRegistration;
    private String unserviceableStatus;
    @Size(max = 8000)
    private String remarks;
    private Set<String> attachment;
    @NotNull
    private Long locationId;
    private Long storeIssueId;
    private StockRoomType stockRoomType = StockRoomType.STORE_RETURN_CONSUMABLE;
    private PartClassification partClassification = PartClassification.ROTABLE;
    private Long storeStockRoomId;
    private Long departmentId;
    @NotNull
    private Boolean isServiceable;
    private String workOrderNumber;
    private String workOrderSerial;
    private String storeLocation;
    @Valid
    @NotEmpty
    List<StoreReturnPartRequestDto> storeReturnPartList;
}
