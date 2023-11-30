package com.digigate.engineeringmanagement.planning.payload.response;

import com.digigate.engineeringmanagement.storemanagement.payload.projection.AlternatePartProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StockCardVM {
    private Long id;
    private String partNo;
    private String description;
    private String uomCode;
    private String rackCode;
    private String rackRowCode;
    private String rackRowBinCode;
    private String officeCode;
    private String otherLocation;
    private String aircraftModelName;
    private String number;
    private Integer minStock;
    private Integer maxStock;
    private Long stockRoomId;
    private String stockRoomCode;
    private String icName;
    private List<AlternatePartProjection> alternatePart = new ArrayList<>();
    private List<StockData> stockDataList = new ArrayList<>();
}
