package com.digigate.engineeringmanagement.configurationmanagement.repository.administration;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.WorkFlowAction;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface WorkFlowActionRepository extends AbstractRepository<WorkFlowAction> {

    List<WorkFlowAction> findByIsActiveTrueAndIdInOrShowFalse(Set<Long> workFlowActionIds);

    boolean existsByOrderNumberAndIsActiveTrueAndIdNot(Integer orderNumber, Long id);

    boolean existsByNameAndIsActiveTrueAndIdNot(String name, Long id);

    Optional<WorkFlowAction> findTop1ByShowFalseOrderByOrderNumberDesc();

    boolean existsByNameAndIsActiveTrue(String name);

    boolean existsByOrderNumberAndIsActiveTrue(Integer orderNumber);
}
