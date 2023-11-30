package com.digigate.engineeringmanagement.common.repository;

import com.digigate.engineeringmanagement.common.entity.Role;
import com.digigate.engineeringmanagement.common.payload.request.RoleDetailViewModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByIdAndIsDeletedFalse(Integer id);
    Boolean existsByName(String name);

    @Query("SELECT new  com.digigate.engineeringmanagement.common.payload.request.RoleDetailViewModel("
    +" r.id, r.name ) FROM Role r WHERE r.isDeleted = false ")
    List<RoleDetailViewModel>findAllActiveRole();
}
