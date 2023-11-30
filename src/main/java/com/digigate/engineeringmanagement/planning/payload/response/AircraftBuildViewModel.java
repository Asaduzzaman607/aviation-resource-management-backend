package com.digigate.engineeringmanagement.planning.payload.response;


import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * AircraftBuildViewModel
 *
 * @author Masud Rana
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AircraftBuildViewModel {
    private Long id;
    private Long aircraftId;
    private String aircraftName;
    private Long higherModelId;
    private String higherModelName;
    private Long modelId;
    private String modelName;
    private String higherSerialNo;
    private String serialNo;
    private Double tsnHour;
    private Integer tsnCycle;
    private Boolean isTsnAvailable;
    private Double tsoHour;
    private Integer tsoCycle;
    private Boolean isOverhauled;
    private Double tslsvHour;
    private Integer tslsvCycle;
    private Boolean isShopVisited;
    private LocalDate attachDate;
    private LocalDate comManufactureDate;
    private LocalDate comCertificateDate;
    private LocalDateTime createdAt;
    private Boolean isActive;
    private Long positionId;
    private String positionName;
    private Long locationId;
    private String locationName;
    private Long partId;
    private String partNo;
    private Long higherPartId;
    private String higherPartNo;

    private Long serialId;
    private Long higherSerialId;

    private Double aircraftInHour;

    private Integer aircraftInCycle;

    private LocalDate outDate;

    private String  inRefMessage;

    private String  outRefMessage;

    private String  removalReason;

    private Double aircraftOutHour;

    private Integer aircraftOutCycle;

    private String authNo;
    private String sign;

    public AircraftBuildViewModel(Long id, Long aircraftId, String aircraftName, String higherModelName,
                                  String modelName, String higherSerialNo, String serialNo, Boolean isActive,
                                  Long positionId, String partNo, String higherPartNo) {
        this.id = id;
        this.aircraftId = aircraftId;
        this.aircraftName = aircraftName;
        this.higherModelName = higherModelName;
        this.modelName = modelName;
        this.higherSerialNo = higherSerialNo;
        this.serialNo = serialNo;
        this.isActive = isActive;
        this.positionId = positionId;
        this.partNo = partNo;
        this.higherPartNo = higherPartNo;
    }
}
