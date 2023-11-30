package com.digigate.engineeringmanagement.procurementmanagement.repository;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.procurementmanagement.dto.projection.CsDetailProjection;
import com.digigate.engineeringmanagement.procurementmanagement.entity.CsDetail;

import java.util.List;
import java.util.Set;

public interface CsDetailRepository extends AbstractRepository<CsDetail> {

    List<CsDetail> findByComparativeStatementIdIn(Set<Long> csIdSet);

    List<CsDetailProjection> findCsDetailByIdIn(Set<Long> csDetailIds);
}
