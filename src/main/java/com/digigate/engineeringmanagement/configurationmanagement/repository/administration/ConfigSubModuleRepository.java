package com.digigate.engineeringmanagement.configurationmanagement.repository.administration;

import com.digigate.engineeringmanagement.configurationmanagement.entity.ConfigSubModule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConfigSubModuleRepository extends JpaRepository<ConfigSubModule,Long> {
    List<ConfigSubModule> findByModuleIdAndSubmoduleNameIgnoreCase(Long moduleId, String submoduleName);
    boolean existsByModuleIdAndIsActiveTrue(Long moduleId);

    Optional<ConfigSubModule> findByIdAndIsActiveTrue(Long id);

    Page<ConfigSubModule> findAll(Specification<ConfigSubModule> customSpecification, Pageable pageable);
}
