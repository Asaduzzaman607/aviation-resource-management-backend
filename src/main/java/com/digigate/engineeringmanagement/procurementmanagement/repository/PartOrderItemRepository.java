package com.digigate.engineeringmanagement.procurementmanagement.repository;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.procurementmanagement.dto.projection.PartOrderItemProjection;
import com.digigate.engineeringmanagement.procurementmanagement.dto.projection.PoItemAirCraftProjection;
import com.digigate.engineeringmanagement.procurementmanagement.entity.PartOrderItem;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface PartOrderItemRepository extends AbstractRepository<PartOrderItem> {
    List<PartOrderItem> findByPartOrderId(Long id);
    List<PartOrderItemProjection> findPartOrderItemByPartOrderId(Long poId);
    List<PartOrderItemProjection> findPartOrderItemByPartOrderIdAndIsActiveTrue(Long poId);
    List<PartOrderItemProjection> findPartOrderItemByIdIn(Set<Long> poItemIds);

    @Query(value = "select ac.id as airCraftId,ac.name as aircraftName from part_orders as po\n" +
            "\tinner join  comparative_statement_details  as csd\n" +
            "\ton po.cs_detail_id = csd.id \n" +
            "\tinner join vendor_quotations as vq\n" +
            "\ton csd.quote_id = vq.id \n" +
            "\tinner join quote_requests as rfq\n" +
            "\ton vq.rfq_id = rfq.id \n" +
            "\tinner join procurement_requisitions pr \n" +
            "\ton rfq.requisition_id = pr.id \n" +
            "\tinner join store_demands as sd \n" +
            "\ton pr.store_demand_id = sd.id \n" +
            "\tinner  join aircrafts ac \n" +
            "\ton sd.aircraft_id = ac.id \n" +
            "\twhere po.id =:partOrderId",nativeQuery = true
    )
    PoItemAirCraftProjection findAircraftNameForProcurement(Long partOrderId);

    @Query(value = "select ac.id as airCraftId,ac.name as aircraftName from part_orders as lpo \n" +
            "inner join comparative_statement_details as lcsd on lpo.cs_detail_id = lcsd.id \n" +
            "inner join vendor_quotations as lvq on lcsd.quote_id = lvq.id \n" +
            "inner join quote_requests as lrfq on lvq.rfq_id = lrfq.id \n" +
            "inner join part_orders as po on lrfq.part_order_id = po.id \n" +
            "inner join comparative_statement_details as csd on po.cs_detail_id = csd.id \n" +
            "inner join vendor_quotations as vq on csd.quote_id = vq.id \n" +
            "inner join quote_requests as rfq on vq.rfq_id = rfq.id \n" +
            "inner join procurement_requisitions pr on rfq.requisition_id = pr.id \n" +
            "inner join store_demands as sd on pr.store_demand_id = sd.id \n" +
            "inner join aircrafts ac on sd.aircraft_id = ac.id \n" +
            "where lpo.id = :partOrderId",nativeQuery = true
    )
    PoItemAirCraftProjection findAircraftNameForLogistics(Long partOrderId);


}
