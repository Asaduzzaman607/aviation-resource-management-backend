package com.digigate.engineeringmanagement.storemanagement.repository.partsreceive;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.storemanagement.entity.partsreceive.StoreReceivedGood;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreReceivedGoodRepository extends AbstractRepository<StoreReceivedGood> {
    boolean existsByStoreStockInwardIdAndIsActiveTrue(Long id);
    boolean existsByRequisitionIdAndIsActiveTrue(Long id);
}
