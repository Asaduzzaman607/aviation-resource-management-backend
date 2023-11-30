package com.digigate.engineeringmanagement.procurementmanagement.service;

import com.digigate.engineeringmanagement.common.payload.IDto;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.procurementmanagement.dto.projection.CsDetailProjection;
import com.digigate.engineeringmanagement.procurementmanagement.entity.CsDetail;
import com.digigate.engineeringmanagement.procurementmanagement.repository.CsDetailRepository;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class CsDetailService extends AbstractSearchService<CsDetail, IDto, IdQuerySearchDto> {
    private final CsDetailRepository repository;

    public CsDetailService(CsDetailRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    protected Specification<CsDetail> buildSpecification(IdQuerySearchDto searchDto) {
        return null;
    }

    @Override
    protected <T> T convertToResponseDto(CsDetail csDetail) {
        return null;
    }

    @Override
    protected CsDetail convertToEntity(IDto iDto) {
        return null;
    }

    @Override
    protected CsDetail updateEntity(IDto dto, CsDetail entity) {
        return null;
    }

    public List<CsDetail> findByCsIdIn(Set<Long> csIdSet) {
        return repository.findByComparativeStatementIdIn(csIdSet);
    }

    public List<CsDetailProjection> findCsDetailByIdIn(Set<Long> csDetailIds) {
        return repository.findCsDetailByIdIn(csDetailIds);
    }
}
