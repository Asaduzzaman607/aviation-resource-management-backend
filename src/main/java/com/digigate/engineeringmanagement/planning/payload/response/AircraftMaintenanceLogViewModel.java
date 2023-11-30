package com.digigate.engineeringmanagement.planning.payload.response;

import com.digigate.engineeringmanagement.planning.constant.AmlType;
import com.digigate.engineeringmanagement.planning.payload.request.AmlOilRecordDto;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AircraftMaintenanceLogViewModel {
    private Long aircraftMaintenanceLogId;
    private Long aircraftId;
    private String aircraftName;
    private Long fromAirportId;
    private String fromAirportIataCode;
    private Long toAirportId;
    private String toAirportIataCode;
    private Long preFlightInspectionAirportId;
    private Long captainId;
    private String captainName;
    private Long firstOfficerId;
    private String firstOfficerName;
    private String preFlightInspectionIataCode;
    private LocalDateTime pfiTime;
    private LocalDateTime ocaTime;
    private Integer previousPageNo;
    private Integer pageNo;
    private Character alphabet;
    private String flightNo;
    private LocalDate date;
    private Double refuelDelivery;
    private Double specificGravity;
    private Double convertedIn;
    private String remarks;
    private AmlType amlType;
    private Boolean isActive;
    List<AmlSignatureViewModel> signatureList;
    private AmlFlightViewModel amlFlightDataViewModel;
    private List<AmlOilRecordDto> amlOilRecordViewModels;
    private List<AmlDefectRectificationModelView> rectificationViewModels;
}
