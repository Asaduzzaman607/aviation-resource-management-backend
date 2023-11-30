package com.digigate.engineeringmanagement.planning.service;

import com.digigate.engineeringmanagement.planning.entity.AmlOilRecord;
import com.digigate.engineeringmanagement.planning.payload.request.AmlOilRecordDto;
import com.digigate.engineeringmanagement.planning.payload.request.AmlRecordRequest;
import com.digigate.engineeringmanagement.planning.payload.request.OilRecordSearchDto;

import java.util.List;
import java.util.Set;

public interface AmlOilRecordIService {
    List<AmlOilRecordDto> getOilRecordByAmlId(OilRecordSearchDto oilRecordSearchDto);
    void saveAllRecords(AmlRecordRequest amlRecordRequest, Long amlId);
    void updateAllRecords(AmlRecordRequest amlRecordRequest, Long amlId);
    List<AmlOilRecord> findAllByAmlIdInAndType(Set<Long> amlIds);

    List<AmlOilRecord> findAllByAmlIdInAndTypeOnArrival(Set<Long> amlIds);
}
