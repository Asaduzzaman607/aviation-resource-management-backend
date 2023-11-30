package com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand;

import com.digigate.engineeringmanagement.planning.payload.response.PartViewModelLite;
import com.digigate.engineeringmanagement.procurementmanagement.dto.projection.PartOrderProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.AlternatePartProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.StorePartSerialViewModelLite;
import lombok.*;

import java.util.List;
import java.util.Set;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StoreReturnPartResponseDto {
    private Long id;
    private String description;
    private Long storeReturnId;
    private Long quantityReturn;
    private boolean serviceable;
    private String cardLineNo;
    private String releaseNo;
    private Long partId;
    private String partNo;
    private Long installedPartId;
    private String installedPartNo;
    private String partDescription;
    private Integer availableQuantity;
    private Long removedPartUomId;
    private String removedPartUomCode;
    private Long installedPartUomId;
    private String installedPartUomCode;
    private String installedPartDescription;
    private List<ReturnPartsDetailViewModel> partsDetailViewModels;

    private List<StorePartSerialViewModelLite> serialViewModelLite;
    private List<PartViewModelLite> lotNumber;
    private List<StorePartAvailablityViewModelLite> otherLocation;
    private Set<AlternatePartProjection> alternatePart;
    private PartOrderProjection partOrderNo;
    private Boolean isInactive;
}
