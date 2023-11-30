package com.digigate.engineeringmanagement.planning.repository;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.planning.entity.Cabin;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Cabin Repository
 *
 * @author Pranoy Das
 */
@Repository
public interface CabinRepository extends AbstractRepository<Cabin> {
    List<Cabin> findAllByCodeOrTitle(Character code, String description);
    List<Cabin> findAllByIsActiveTrue();
    Boolean existsByCode(Character code);
    Boolean existsByTitle(String title);
}
