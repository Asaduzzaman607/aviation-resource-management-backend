package com.digigate.engineeringmanagement.procurementmanagement.util;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.planning.entity.Part;
import com.digigate.engineeringmanagement.planning.payload.response.AlternatePartViewModel;
import com.digigate.engineeringmanagement.procurementmanagement.dto.projection.IqItemProjection;
import com.digigate.engineeringmanagement.procurementmanagement.dto.projection.VqDetailProjection;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class CsPartUtilService {
    /** part id */
    public Long getPartId(IqItemProjection iqItemProjection) {
        if(alternatePartIdNonNull(iqItemProjection)){
            return iqItemProjection.getAltPartId();
        }

        return iqItemProjection.getPartId();
    }

    public Long getPartId(VqDetailProjection vqDetailProjection, Long overload) {
        if(Objects.nonNull(vqDetailProjection.getAlternatePartId())){
            return vqDetailProjection.getAlternatePartId();
        } else if (Objects.nonNull(vqDetailProjection.getPoItemIqItemAlternatePartId())) {
            return vqDetailProjection.getPoItemIqItemAlternatePartId();
        } else if (Objects.nonNull(vqDetailProjection.getRequisitionItemDemandItemPartId())){
            return vqDetailProjection.getRequisitionItemDemandItemPartId();
        } else {
            return vqDetailProjection.getPoItemIqItemRequisitionItemDemandItemPartId();
        }
    }

    /** part no */
    public String getPartNo(IqItemProjection iqItemProjection) {
        if(alternatePartIdNonNull(iqItemProjection)){
            return iqItemProjection.getAltPartNo();
        }

        return iqItemProjection.getPartNo();
    }

    /** part description */
    public String getPartDescription(IqItemProjection iqItemProjection) {
        if(alternatePartIdNonNull(iqItemProjection)){
            return iqItemProjection.getAltPartDescription();
        }

        return iqItemProjection.getPartDescription();
    }

    /** part -> unit of measurement id */
    public Long getUomId(IqItemProjection iqItemProjection) {
        if(Objects.nonNull(iqItemProjection.getUomId()))
        {
            return iqItemProjection.getUomId();
        }
        else if (alternatePartIdNonNull(iqItemProjection)){
            return iqItemProjection.getAltPartUomId();
        }

        return iqItemProjection.getPartUomId();
    }

    public Long getUomId(IqItemProjection iqItemProjection, Long overload) {
        if (alternatePartIdNonNull(iqItemProjection)){
            return iqItemProjection.getAltPartUomId();
        }

        return iqItemProjection.getPartUomId();
    }

    /** part -> unit of measurement code */
    public String getUomCode(IqItemProjection iqItemProjection) {
        if(Objects.nonNull(iqItemProjection.getUomId()))
        {
            return iqItemProjection.getUomCode();
        }
        if(alternatePartIdNonNull(iqItemProjection)){
            return iqItemProjection.getAltPartUomCode();
        }

        return iqItemProjection.getPartUomCode();
    }

    public String getUomCode(IqItemProjection iqItemProjection, Long overload) {
        if(alternatePartIdNonNull(iqItemProjection)){
            return iqItemProjection.getAltPartUomCode();
        }

        return iqItemProjection.getPartUomCode();
    }

    /** alternate part list */
    public List<AlternatePartViewModel> getAlternatePart(IqItemProjection iqItemProjection,
                                                         Map<Long, Set<Part>> alternateMap) {
        if(alternatePartIdNonNull(iqItemProjection)){
            return getAlternatePartViewModels(iqItemProjection.getAltPartId(), alternateMap);
        }
        else if(partIdNonNull(iqItemProjection)){
            return getAlternatePartViewModels(iqItemProjection.getPartId(), alternateMap);
        }

        return new ArrayList<>();
    }

    /** presence of part */
    private boolean partIdNonNull(IqItemProjection iqItemProjection){
        return Objects.nonNull(iqItemProjection.getPartId());
    }

    /** presence of alternate part */
    private boolean alternatePartIdNonNull(IqItemProjection iqItemProjection){
        return Objects.nonNull(iqItemProjection.getAltPartId());
    }

    private List<AlternatePartViewModel> getAlternatePartViewModels(Long partId, Map<Long, Set<Part>> alternateMap) {
        Set<Part> parts = alternateMap.getOrDefault(partId, new HashSet<>());
        return parts.stream().map(this::populateAlternatePartViewModel).collect(Collectors.toList());
    }

    private AlternatePartViewModel populateAlternatePartViewModel(Part part) {
        return AlternatePartViewModel.builder()
                .id(part.getId())
                .partNo(part.getPartNo())
                .build();
    }
}
