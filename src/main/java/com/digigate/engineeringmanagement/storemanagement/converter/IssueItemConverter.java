package com.digigate.engineeringmanagement.storemanagement.converter;

import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.StoreDemandItem;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.StoreIssue;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.StoreIssueItem;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.StoreIssueItemDto;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.UnitMeasurement;
public class IssueItemConverter {

    public static StoreIssueItem convertToEntity(StoreIssueItemDto itemDto, StoreDemandItem demandItem, StoreIssue storeIssue,
                                                 UnitMeasurement unitMeasurement) {
        return StoreIssueItem.builder()
                .storeDemandItem(demandItem)
                .unitMeasurement(unitMeasurement)
                .storeIssue(storeIssue)
                .issuedQuantity(itemDto.getIssuedQuantity())
                .remark(itemDto.getRemark())
                .cardLineNo(itemDto.getCardLineNo())
                .priority(itemDto.getPriorityType())
                .build();
    }

    public static StoreIssueItem updateEntity(StoreIssueItem storeIssueItem,
                                              StoreIssueItemDto dto, UnitMeasurement unitMeasurement) {
        storeIssueItem.setIssuedQuantity(dto.getIssuedQuantity());
        storeIssueItem.setPriority(dto.getPriorityType());
        storeIssueItem.setUnitMeasurement(unitMeasurement);
        storeIssueItem.setRemark(dto.getRemark());
        storeIssueItem.setCardLineNo(dto.getCardLineNo());
        return storeIssueItem;
    }
}
