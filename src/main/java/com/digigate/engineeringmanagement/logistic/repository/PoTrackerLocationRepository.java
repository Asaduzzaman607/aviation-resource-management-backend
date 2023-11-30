package com.digigate.engineeringmanagement.logistic.repository;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.logistic.entity.PoTrackerLocation;

import java.util.List;
import java.util.Set;

public interface PoTrackerLocationRepository extends AbstractRepository<PoTrackerLocation> {
    List<PoTrackerLocation> findByPoTrackerIdInAndIsActiveTrue(Set<Long> id);
}
