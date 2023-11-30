package com.digigate.engineeringmanagement.storemanagement.repository.storeconfiguration;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.Room;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.RackProjection;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface RoomRepository extends AbstractRepository<Room> {
    boolean existsByOfficeIdAndIsActiveTrue(Long id);

    Set<RackProjection> findByIdIn(Set<Long> ids);

    List<Room> findByOfficeIdAndCodeIgnoreCaseAndIsActiveTrue(Long officeId, String code);

    List<Room> findByIdIn(List<Long> ids);
}
