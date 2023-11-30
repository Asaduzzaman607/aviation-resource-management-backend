package com.digigate.engineeringmanagement.storemanagement.repository.storedemand;

import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.StoreIssueSerial;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.StoreIssueSerialProjection;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface StoreIssueSerialRepository extends CrudRepository<StoreIssueSerial, Long> {

   List<StoreIssueSerialProjection> findStoreIssueSerialByStoreIssueItemIdIn(Set<Long> storeIssueIds);
   List<StoreIssueSerial> findAllByStoreIssueItemIdIn(Set<Long> storeIssueIds);
   List<StoreIssueSerial> findAllByIsActiveTrue();
   void deleteAllByStoreIssueItemIdIn(Set<Long> ids);
}
