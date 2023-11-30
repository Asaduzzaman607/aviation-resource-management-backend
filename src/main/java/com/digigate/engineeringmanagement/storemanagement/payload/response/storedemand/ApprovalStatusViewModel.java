package com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand;

import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.WorkFlowAction;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.ApprovalStatus;
import lombok.*;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;
import java.util.Objects;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApprovalStatusViewModel {
    private Long id;
    private Long updatedBy;
    private String updatedByName;
    private String updatedByDesignation;
    private Integer orderNumber;
    private String workFlowAction;
    private Long workFlowActionId;

    @Override
    public String toString() {
        return "updatedBy='" + updatedBy + '\'' +
                ", workFlowAction='" + workFlowAction + '\'' +
                '}';
    }

    public static ApprovalStatusViewModel from(ApprovalStatus approvalStatus,
                                               Map<Long, Pair<Pair<String, String>, WorkFlowAction>> nameMap) {

        if (Objects.isNull(nameMap.get(approvalStatus.getId())) || Objects.isNull(nameMap.get(approvalStatus.getId()).getValue())) {
            return ApprovalStatusViewModel.builder().id(approvalStatus.getId())
                .workFlowActionId(approvalStatus.getWorkFlowActionId()).build();
        }

        Pair<Pair<String, String>, WorkFlowAction> pair = nameMap.get(approvalStatus.getId());
        WorkFlowAction workFlowAction = pair.getValue();
        return ApprovalStatusViewModel.builder()
                .id(approvalStatus.getId())
                .workFlowActionId(approvalStatus.getWorkFlowActionId())
                .updatedBy(approvalStatus.getUpdatedBy())
                .updatedByName(pair.getKey().getKey())
                .updatedByDesignation(pair.getKey().getValue())
                .workFlowAction(workFlowAction.getName())
                .orderNumber(workFlowAction.getOrderNumber())
                .build();
    }
}
