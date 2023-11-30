package com.digigate.engineeringmanagement.procurementmanagement.repository;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.procurementmanagement.entity.CsAuditDisposal;

import java.util.List;

public interface CsAuditDisposalRepository extends AbstractRepository<CsAuditDisposal> {
    List<CsAuditDisposal> findByCsPartDetailId(Long id);
}
