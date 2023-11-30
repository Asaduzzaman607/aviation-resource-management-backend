package com.digigate.engineeringmanagement.storeinspector.repository.storeinspector;

import com.digigate.engineeringmanagement.common.constant.VendorWorkFlowType;
import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.storeinspector.payload.projection.InspectionChecklistProjection;
import com.digigate.engineeringmanagement.storeinspector.entity.storeinspector.InspectionChecklist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface InspectionChecklistRepository extends AbstractRepository<InspectionChecklist> {
    List<InspectionChecklist> findByDescription(String description);

    List<InspectionChecklistProjection> findDescriptionByIdIn(Set<Long> idSet);

    List<InspectionChecklistProjection> findByIdIn(List<Long> descriptionId);

    Page<InspectionChecklist> findAllByIsActiveAndWorkFlowActionIdInAndDescriptionContains
            (Boolean isActive, Set<Long> workflowIds, String query, Pageable pageable);

    Page<InspectionChecklist> findAllByIsActiveAndWorkFlowActionIdAndDescriptionContains
            (Boolean isActive, Long approvedId, String query, Pageable pageable);

    Page<InspectionChecklist> findAllByIsActiveAndDescriptionContains
            (Boolean isActive, String query, Pageable pageable);

    Page<InspectionChecklist> findAllByIsRejectedTrueAndWorkflowTypeAndDescriptionContains(VendorWorkFlowType vendorWorkFlowType, String query, Pageable pageable);

    Page<InspectionChecklist> findAllByIsRejectedFalseAndIsActiveAndWorkFlowActionIdInAndWorkflowTypeAndDescriptionContains
            (Boolean isActive, Set<Long> workflowIds, VendorWorkFlowType vendorWorkFlowType, String query, Pageable pageable);

}
