package com.digigate.engineeringmanagement.logistic.repository;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.logistic.entity.DutyFeeItem;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Repository
public interface DutyFeeItemRepository extends AbstractRepository<DutyFeeItem> {
    Set<DutyFeeItem> findByDutyFeeIdInAndIsActiveTrue(Set<Long> dutyFeeIds);

    List<DutyFeeItem> findByDutyFeeIdAndIsActiveTrue(Long parentId);

    List<DutyFeeItem> findByDutyFeeId(Long id);
}
