package com.digigate.engineeringmanagement.configurationmanagement.repository.configuration;

import com.digigate.engineeringmanagement.configurationmanagement.dto.projection.CityProjection;
import com.digigate.engineeringmanagement.configurationmanagement.dto.projection.CountryProjection;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Country;
import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends AbstractRepository<Country> {

    List<Country> findByNameIgnoreCaseOrCodeIgnoreCaseOrDialingCode(String name, String code,
        String dialingCode);
    Set<CountryProjection> findByIdIn(Set<Long> countryIds);
    Optional<Country> findByIdAndIsActiveTrue(Long id);
}
