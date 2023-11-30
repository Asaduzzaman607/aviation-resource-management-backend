package com.digigate.engineeringmanagement.planning.repository;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.planning.constant.OilRecordTypeEnum;
import com.digigate.engineeringmanagement.planning.entity.AmlOilRecord;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * AML Oil Record repository
 *
 * @author Sayem Hasnat
 */
@Repository
public interface AmlOilRecordRepository extends AbstractRepository<AmlOilRecord> {
    @Query("select a from AmlOilRecord a where a.amlId = :amlId")
    List<AmlOilRecord> findByAmlId(@Param("amlId") Long id);

    @Query("select a.id from AmlOilRecord a where " +
            "a.type = :type " +
            "and a.amlId = :amlId")
    Optional<Long> findByTypeAndAmlId(@Param("type") OilRecordTypeEnum type,
                                      @Param("amlId") Long id);

    @Query("select new AmlOilRecord( " +
            "a.amlId, " +
            "a.engineOil1, " +
            "a.engineOil2," +
            "a.apuOil )" +
            "from AmlOilRecord a " +
            "where a.aircraftMaintenanceLog.id in :amlIds " +
            "and a.type = :type and a.isActive = true")
    List<AmlOilRecord> findByAmlIdAndType(Set<Long> amlIds, OilRecordTypeEnum type);

    @Query("select a from AmlOilRecord a where " +
            "a.amlId = :amlId " +
            "and a.isActive = :isActive")
    List<AmlOilRecord> findByAmlIdAndIsActive(@Param("amlId") Long id,
                                              @Param("isActive") Boolean isActive);

    List<AmlOilRecord> findAllByAmlIdInAndType(Set<Long> amlIds, OilRecordTypeEnum uplift);

    @Modifying
    void deleteAllByAircraftMaintenanceLogId(Long amlId);
}
