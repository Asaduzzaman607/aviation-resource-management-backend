package com.digigate.engineeringmanagement.storemanagement.service.storedemand;

import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.StoreDemandItem;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.StoreIssue;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.StoreIssueItem;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.UnitMeasurement;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.StorePartSerial;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.GrnAndSerialDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.StoreIssueItemDto;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface StoreIssueItemDetailsService {

    StoreIssueItem create(StoreIssueItemDto dto,
                          StoreDemandItem demandItem,
                          StoreIssue issue,
                          Set<GrnAndSerialDto> serialGrnDtos, Map<Long,
            StorePartSerial> storePartSerialMap, UnitMeasurement unitMeasurement);

    StoreIssueItem update(StoreIssueItemDto dto, StoreDemandItem storeDemandItem, Set<GrnAndSerialDto> serialGrnDtos, Map<Long, StorePartSerial> storePartSerialMap, UnitMeasurement unitMeasurement);

    StoreIssueItem findById(Long id);

}
