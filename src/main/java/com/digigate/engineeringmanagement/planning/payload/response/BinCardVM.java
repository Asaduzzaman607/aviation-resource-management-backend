package com.digigate.engineeringmanagement.planning.payload.response;

import com.digigate.engineeringmanagement.planning.constant.PartClassification;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.AlternatePartProjection;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BinCardVM {
    private Long id;
    private String partNo;
    private PartClassification partClassification;
    private String description;
    private String uomCode;
    private String rackCode;
    private String rackRowCode;
    private String rackRowBinCode;
    private String otherLocation;
    private String officeCode;
    private String aircraftModelName;
    private String number;
    private Integer minStock;
    private List<AlternatePartProjection> alternatePart = new ArrayList<>();
    private List<BinData> binDataList = new ArrayList<>();
}
