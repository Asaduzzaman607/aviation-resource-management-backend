package com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand;

import com.digigate.engineeringmanagement.planning.constant.PartClassification;
import com.digigate.engineeringmanagement.storemanagement.constant.LocationTag;
import lombok.*;

import javax.validation.constraints.Size;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StorePartAvailabilityResponseDto {

    private Long id;
    private Long partId;
    private String partNo;
    private PartClassification partClassification;
    private String acType;
    private Long acTypeId;
    private Long officeId;
    private String officeCode;
    private Long roomId;
    private String roomCode;
    private Long rackId;
    private String rackCode;
    private Long rackRowId;
    private String rackRowCode;
    private Long rackRowBinId;
    private String rackRowBinCode;
    private String otherLocation;
    private LocationTag locationTag;
    private PartClassification partType;
    private Integer quantity;
    private Integer demandQuantity;
    private Integer issuedQuantity;
    private Integer requisitionQuantity;
    private Integer uomWiseQuantity;
    private Integer minStock;
    private Integer maxStock;
    private Long uomId;
    @Size(max = 200)
    private String uomCode;
    private Long stockRoomId;
    private String stockRoomCode;
}
