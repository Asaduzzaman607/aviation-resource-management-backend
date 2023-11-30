package com.digigate.engineeringmanagement.storemanagement.repository.storedemand;

import com.digigate.engineeringmanagement.procurementmanagement.dto.projection.ItemProjection;
import com.digigate.engineeringmanagement.procurementmanagement.dto.response.RfqPartViewModel;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.ProcurementRequisitionItem;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.RequisitionItemProjection;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ProcurementRequisitionItemRepository extends CrudRepository<ProcurementRequisitionItem, Long> {
    List<RequisitionItemProjection> findProcurementRequisitionItemByIdIn(Set<Long> requisitionItemIds);

    List<ProcurementRequisitionItem> findByRequisitionId(Long requisitionId);

    List<ProcurementRequisitionItem> findAllByIdInAndIsActiveTrue(Set<Long> itemIdSet);

    List<ItemProjection> findProcurementRequisitionItemByRequisitionId(Long requisitionId);

    List<ItemProjection> findByIdIn(Set<Long> itemIdSet);

    List<ProcurementRequisitionItem> findByRequisitionIdIn(Set<Long> requisitionIds);

    ItemProjection findProcurementRequisitionItemById(Long id);

    List<ProcurementRequisitionItem> findByDemandItemId(Long id);
}
