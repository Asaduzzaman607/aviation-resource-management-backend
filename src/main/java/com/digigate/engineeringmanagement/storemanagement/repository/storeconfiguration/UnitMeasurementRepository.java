package com.digigate.engineeringmanagement.storemanagement.repository.storeconfiguration;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.UnitMeasurement;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.UnitMeasurementProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface UnitMeasurementRepository extends AbstractRepository<UnitMeasurement> {
    UnitMeasurement findByCodeIgnoreCase(String code);
    Set<UnitMeasurementProjection> findUnitMeasurementByIdIn(Set<Long> unitMeasurementIds);
    List<UnitMeasurement> findUnitMeasurementByIdInAndIsActiveTrue(List<Long> unitMeasurementIds);
    UnitMeasurementProjection findUnitMeasurementById(Long id);
    List<UnitMeasurementProjection> findByIdIn(List<Long> ids);

    @Query("select um from UnitMeasurement um where um.isActive = true")
    Set<UnitMeasurement> findAllUnitOfMeasures();

    List<UnitMeasurement> findAllByCodeIn(Set<String> codeList);

}
