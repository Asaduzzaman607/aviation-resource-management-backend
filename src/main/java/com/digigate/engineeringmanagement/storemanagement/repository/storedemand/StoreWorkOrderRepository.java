package com.digigate.engineeringmanagement.storemanagement.repository.storedemand;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.StoreWorkOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface StoreWorkOrderRepository extends AbstractRepository<StoreWorkOrder> {
    @Query("select s.workOrderNo from StoreWorkOrder s where s.unserviceablePart.id = :unserviceablePartId")
    String findWorkOrderNoByUnserviceablePartId(Long unserviceablePartId);

    Page<StoreWorkOrder> findAllByIsRejectedFalseAndIsActiveAndWorkFlowActionIdInAndWorkOrderNoContains(
            Boolean isActive, Set<Long> pendingSearchWorkFlowIds, String query, Pageable pageable);

    Page<StoreWorkOrder> findAllByIsActiveAndWorkFlowActionIdAndWorkOrderNoContains(Boolean isActive, Long approvedId, String query, Pageable pageable);

    Page<StoreWorkOrder> findAllByIsRejectedTrueAndWorkOrderNoContains(String query, Pageable pageable);

    Page<StoreWorkOrder> findAllByIsActiveAndWorkOrderNoContains(Boolean isActive, String query, Pageable pageable);
}
