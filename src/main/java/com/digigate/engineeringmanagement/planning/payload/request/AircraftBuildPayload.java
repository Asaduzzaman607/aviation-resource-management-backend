package com.digigate.engineeringmanagement.planning.payload.request;


import com.digigate.engineeringmanagement.common.payload.IDto;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AircraftBuildPayload implements IDto {
    @NotNull
    private Long aircraftId;
    @NotNull
    private Long higherModelId;
    @NotNull
    private Long modelId;
    @NotNull
    private Long higherSerialId;
    @NotNull
    private Long serialId;
    @PositiveOrZero
    private Double tsnHour;
    @PositiveOrZero
    private Integer tsnCycle;
    private Boolean isTsnAvailable = true;
    @PositiveOrZero
    private Double tsoHour;
    @PositiveOrZero
    private Integer tsoCycle;
    private Boolean isOverhauled;
    @PositiveOrZero
    private Double tslsvHour;
    @PositiveOrZero
    private Integer tslsvCycle;
    private Boolean isShopVisited;
    @NotNull
    private LocalDate attachDate;
    private LocalDate comManufactureDate;
    private LocalDate comCertificateDate;
    private Long positionId;
    @NotNull
    private Long locationId;
    @NotNull
    private Long partId;
    @NotNull
    private Long higherPartId;

    @NotNull
    private Double aircraftInHour;

    @NotNull
    private Integer aircraftInCycle;

    private String  inRefMessage;

    private String authNo;
    private String sign;
}
