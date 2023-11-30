package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.payload.IDto;
import com.digigate.engineeringmanagement.planning.constant.AmlType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AircraftMaintenanceLogDto implements IDto {
   private Long aircraftMaintenanceLogId;
   private Long referenceAmlId;
   @NotNull(message = ErrorId.AIRCRAFT_ID_IS_REQUIRED)
   private Long aircraftId;
   private Long fromAirportId;
   private Long toAirportId;
   private Long captainId;
   private Long firstOfficerId;
   private Long preFlightInspectionAirportId;
   @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
   private LocalDateTime pfiTime;
   @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
   private LocalDateTime ocaTime;
   private Integer pageNo;
   private Character alphabet;
   private String flightNo;
   @NotNull
   private LocalDate date;
   private Double refuelDelivery;
   private Double specificGravity;
   private Double convertedIn;
   private String remarks;
   private AmlType amlType;
   private Boolean saveOilRecord;
   private List<MaintenanceLogSignatureDto> maintenanceLogSignatureDtoList;
   private AmlFlightDataDto amlFlightData;
   private AmlRecordRequest amlOilRecord;
   private Boolean needToSaveDefectRectification = false;
   private List<AMLDefectRectificationDto> defectRectifications;
}
