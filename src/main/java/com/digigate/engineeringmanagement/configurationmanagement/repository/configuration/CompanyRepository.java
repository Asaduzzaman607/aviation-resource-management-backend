package com.digigate.engineeringmanagement.configurationmanagement.repository.configuration;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Company;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.CompanyProjection;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

;

@Repository
public interface CompanyRepository extends AbstractRepository<Company> {
    List<Company> findByCompanyName(String companyName);
    boolean existsByCompanyName(String companyName);
    boolean existsByCityIdAndIsActiveTrue(Long cityId);
    CompanyProjection findCompanyById(Long id);
    List<CompanyProjection> findCompanyByIdIn(Set<Long> idSet);
}
