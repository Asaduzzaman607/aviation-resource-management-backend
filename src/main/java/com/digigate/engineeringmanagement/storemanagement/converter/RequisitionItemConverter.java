package com.digigate.engineeringmanagement.storemanagement.converter;

import com.digigate.engineeringmanagement.procurementmanagement.constant.InputType;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.ProcurementRequisition;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.ProcurementRequisitionItem;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.StoreDemandItem;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.ProcurementRequisitionItemDto;

import java.util.Objects;

public class RequisitionItemConverter {

    public static ProcurementRequisitionItem convertToEntity(ProcurementRequisitionItemDto itemDto,
                                                             StoreDemandItem demandItem,
                                                             ProcurementRequisition requisition) {
        return ProcurementRequisitionItem.builder()
            .id(itemDto.getId())
            .demandItem(demandItem)
            .procurementRequisition(requisition)
            .requisitionQuantity(itemDto.getQuantityRequested())
            .remark(itemDto.getRemark())
            .isActive(true)
            .priority(itemDto.getPriorityType())
            .build();
    }

    public static ProcurementRequisitionItem updateEntity(ProcurementRequisitionItem requisitionItem,
                                                          ProcurementRequisitionItemDto dto) {
        if(Objects.nonNull(dto.getRequisition())){
            requisitionItem.setProcurementRequisition(dto.getRequisition());
        }
        requisitionItem.setRequisitionQuantity(dto.getQuantityRequested());
        requisitionItem.setPriority(dto.getPriorityType());
        if(dto.getInputType() == InputType.MANUAL){
            requisitionItem.setDemandItem(dto.getStoreDemandItem());
        }
        requisitionItem.setRemark(dto.getRemark());
        return requisitionItem;
    }
}
