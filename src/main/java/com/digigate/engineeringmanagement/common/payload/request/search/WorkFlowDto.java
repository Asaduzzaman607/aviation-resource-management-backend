package com.digigate.engineeringmanagement.common.payload.request.search;

import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.WorkFlowAction;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.ApprovalStatus;
import lombok.*;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.util.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Component
public class WorkFlowDto {
    private Map<Long, WorkFlowAction> workFlowActionMap = new HashMap<>();
    private Set<Long> actionableIds = new HashSet<>();
    private Set<Long> editableIds = new HashSet<>();
    private Map<Long, Pair<Pair<String, String>, WorkFlowAction>> namesFromApprovalStatuses = new HashMap<>();
    private Map<Long, List<ApprovalStatus>> statusMap = new HashMap<>();
}
