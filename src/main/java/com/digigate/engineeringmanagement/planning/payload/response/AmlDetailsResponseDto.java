package com.digigate.engineeringmanagement.planning.payload.response;

import com.digigate.engineeringmanagement.planning.payload.request.AmlOilRecordDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AmlDetailsResponseDto {
    private AircraftMaintenanceLogViewModel amlResponseData;
    private List<AmlDefectRectificationModelView> defectRectificationResponseDto;
    private AmlFlightViewModel flightResponseDto;
    private List<AmlOilRecordDto> oilRecordData;
}
