package com.digigate.engineeringmanagement.procurementmanagement.service;

import com.digigate.engineeringmanagement.procurementmanagement.dto.request.PoInternalDto;
import com.digigate.engineeringmanagement.procurementmanagement.entity.PartOrder;

import javax.transaction.Transactional;

public interface ProcurementManualPoService {

    @Transactional
    PoInternalDto populateToManualEntity(PoInternalDto rfqVendorRequestDto);

    @Transactional
    PoInternalDto populateToManualEntity(PoInternalDto dto, PartOrder partOrder);
}
