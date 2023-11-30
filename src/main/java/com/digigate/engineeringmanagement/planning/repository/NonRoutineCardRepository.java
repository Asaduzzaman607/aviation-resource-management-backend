package com.digigate.engineeringmanagement.planning.repository;


import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.planning.entity.NonRoutineCard;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Non Routine Card Repository
 *
 * @author ashinisingha
 */
@Repository
public interface NonRoutineCardRepository extends AbstractRepository<NonRoutineCard> {
    @Query("select nrc.id from NonRoutineCard nrc where nrc.nrcNo = :nrcNo")
    Optional<Long> findNrcIdByNrcNo(String nrcNo);
}
