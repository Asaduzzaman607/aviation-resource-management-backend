package com.digigate.engineeringmanagement.storemanagement.repository.storedemand;

import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.StoreIssueItem;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.StoreIssueItemProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface StoreIssueItemRepository extends CrudRepository<StoreIssueItem, Long> {

    List<StoreIssueItemProjection> findByStoreIssueId(Long issueId);

    StoreIssueItemProjection findByStoreDemandItemId(Long demandId);

    List<StoreIssueItem> getAllStoreIssueItemByStoreIssueId(Long issueId);

    @Query(value = "select sum(sii.quantity_issued) as issued from store_issue_items\n" +
           "  as sii where sii.store_demand_id IN\n" +
           "  (select si.store_demand_id from store_issues as si)", nativeQuery = true)
   Long findTotalCountForIssued();

    Set<StoreIssueItem> getAllStoreIssueItemByStoreIssueIdIn(Set<Long> ids);
}
