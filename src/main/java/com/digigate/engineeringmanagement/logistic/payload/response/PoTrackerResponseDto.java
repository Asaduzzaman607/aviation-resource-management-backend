package com.digigate.engineeringmanagement.logistic.payload.response;

import com.digigate.engineeringmanagement.logistic.constant.TrackerStatus;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PoTrackerResponseDto {
    private Long id;
    private Long partOrderItemId;
    private Long partOrderId;
    private String partOrderNo;
    private String trackerNo;
    private TrackerStatus trackerStatus;
    private List<PoTrackerLocationResponseDto> poTrackerLocationList;
    private Set<String> attachment;
}
