package com.digigate.engineeringmanagement.logistic.payload.request;

import com.digigate.engineeringmanagement.common.payload.IDto;
import com.digigate.engineeringmanagement.logistic.constant.TrackerStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PoTrackerRequestDto implements IDto {

    private Long id;
    @NotNull
    private Long partOrderItemId;
    private String trackerNo;
    private TrackerStatus trackerStatus;
    private Set<String> attachment;
    @NotEmpty
    private List<PoTrackerLocationRequestDto> poTrackerLocationList;

}
