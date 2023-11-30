package com.digigate.engineeringmanagement.storemanagement.repository.scrap;

import com.digigate.engineeringmanagement.storemanagement.entity.scrap.StoreScrapPartSerial;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.StoreScrapPartSerialProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface StoreScrapPartSerialRepository extends JpaRepository<StoreScrapPartSerial, Long> {

    List<StoreScrapPartSerial> findAllByStoreScrapPartId(Long partId);

    List<StoreScrapPartSerialProjection> findAllByStoreScrapPartIdIn(Set<Long> ids);
}
