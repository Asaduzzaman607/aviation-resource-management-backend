package com.digigate.engineeringmanagement.storemanagement.repository.storedemand;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.planning.constant.StorePartAvailabilityLogParentType;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.StorePartAvailabilityLog;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StorePartAvailabilityLogRepository extends AbstractRepository<StorePartAvailabilityLog> {
    List<StorePartAvailabilityLog> findByStorePartSerialId(Long storePartSerialId);
}
