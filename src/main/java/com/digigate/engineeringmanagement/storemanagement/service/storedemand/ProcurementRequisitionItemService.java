package com.digigate.engineeringmanagement.storemanagement.service.storedemand;

import com.digigate.engineeringmanagement.procurementmanagement.dto.projection.ItemProjection;
import com.digigate.engineeringmanagement.procurementmanagement.dto.response.RfqPartViewModel;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.ProcurementRequisition;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.ProcurementRequisitionItem;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.StoreDemandItem;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.RequisitionItemProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.ProcurementRequisitionItemDto;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand.ProcurementRequisitionItemViewModel;

import java.util.List;
import java.util.Set;

public interface ProcurementRequisitionItemService {
    ProcurementRequisitionItem create(ProcurementRequisitionItemDto dto, StoreDemandItem demandItem, ProcurementRequisition requisition);

    ProcurementRequisitionItem update(ProcurementRequisitionItemDto dto);

    ProcurementRequisitionItem findById(Long id);

    List<RequisitionItemProjection> findRequisitionItemList(Set<Long> requisitionItemIds);

    List<ProcurementRequisitionItem> findByRequisitionId(Long requisitionId);

    List<ProcurementRequisitionItem> getAllByDomainIdIn(Set<Long> itemIdSet);

    List<ItemProjection> findByProcurementRequisitionId(Long requisitionId);

    List<ItemProjection> findAllByIdIn(Set<Long> itemIdSet);

    List<ProcurementRequisitionItemViewModel> getAllResponseByViewModel(Set<Long> requisitionIds);

    List<ItemProjection> findRequisitionItemByRequisitionId(Long requisitionId);

    RfqPartViewModel findItemById(Long id);

    List<RfqPartViewModel> getRfqPartViewModelLIst(Long requisitionId);
}
