package com.digigate.engineeringmanagement.planning.repository;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.planning.entity.Forecast;
import com.digigate.engineeringmanagement.planning.payload.response.ForecastViewModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Forecast repository
 *
 * @author Masud Rana
 */
@Repository
public interface ForecastRepository extends AbstractRepository<Forecast> {
    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.ForecastViewModel" +
            "(f.id, f.name, f.createdAt, f.isActive) from Forecast f " +
            "where f.isActive = :isActive and (:name is null or f.name like :name%) ")
    Page<ForecastViewModel> findByName(String name, Boolean isActive, Pageable pageable);
}
