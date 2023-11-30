package com.digigate.engineeringmanagement.common.repository;

import com.digigate.engineeringmanagement.common.entity.FeatureRole;
import com.digigate.engineeringmanagement.common.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeatureRoleRepository extends JpaRepository<FeatureRole, Long> {
    List<FeatureRole> findAllByRole(Role role);
}
