package com.digigate.engineeringmanagement.storeinspector.repository.storeinspector;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.storeinspector.entity.storeinspector.InspectionCriterion;

import java.util.List;
import java.util.Set;


public interface InspectionCriterionRepository extends AbstractRepository<InspectionCriterion> {
    boolean existsByInspectionIdAndIsActiveTrue(Long id);

    List<InspectionCriterion> findByInspectionIdInAndIsActiveTrue(Set<Long> inspectionIds);

    boolean existsByDescriptionIdAndIsActiveTrue(Long id);

}
