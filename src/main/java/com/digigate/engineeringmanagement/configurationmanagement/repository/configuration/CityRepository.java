package com.digigate.engineeringmanagement.configurationmanagement.repository.configuration;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.configurationmanagement.dto.projection.CityProjection;
import com.digigate.engineeringmanagement.configurationmanagement.entity.City;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface CityRepository extends AbstractRepository<City> {

    Set<CityProjection> findByIdInAndIsActiveTrue(Set<Long> cityIds);

    boolean existsByCountryIdAndIsActiveTrue(long countryId);

    Set<CityProjection> findByIdIn(Set<Long> cityIds);

    CityProjection findCitiesById(Long id);

    List<City> findByCountryIdAndNameAndIsActiveTrue(Long countryId, String name);
}
