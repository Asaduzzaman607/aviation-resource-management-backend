package com.digigate.engineeringmanagement.storemanagement.repository.storeconfiguration;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.StoreStockRoom;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.StoreStockRoomProjection;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface StoreStockRoomRepository extends AbstractRepository<StoreStockRoom> {
    List<StoreStockRoom> findByCodeIgnoreCase(String storeStockRoomCode);
    List<StoreStockRoomProjection> findStoreStockRoomByIdIn(Set<Long> collect);
}
