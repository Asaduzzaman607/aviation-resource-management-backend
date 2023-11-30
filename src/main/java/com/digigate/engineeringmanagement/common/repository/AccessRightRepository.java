package com.digigate.engineeringmanagement.common.repository;

import com.digigate.engineeringmanagement.common.entity.AccessRight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * Access right repository
 *
 * @author Pranoy Das
 */
@Repository
public interface AccessRightRepository extends JpaRepository<AccessRight, Integer> {
    Set<AccessRight> findAllByIdIn(Set<Integer> accessRightIds);
}
