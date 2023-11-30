package com.digigate.engineeringmanagement.planning.repository;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.planning.entity.Check;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

/**
 * Check Repository
 *
 * @author Ashraful
 */
public interface CheckRepository extends AbstractRepository<Check> {
    @Query("SELECT c.id from Check c where c.title = :title")
    Optional<Long> findIdByTitle(String title);
}
