package com.digigate.engineeringmanagement.storemanagement.service.storedemand;

import com.digigate.engineeringmanagement.storemanagement.payload.projection.OfficeProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.StorePartAvailabilityProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storeconfiguration.RackResponseDto;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storeconfiguration.RackRowBinResponseDto;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storeconfiguration.RackRowResponseDto;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storeconfiguration.RoomResponseDto;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand.StorePartAvailabilityResponseDto;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class StorePartConverter {

    public static StorePartAvailabilityResponseDto convertToStorePartAvailabilityResponseDto(StorePartAvailabilityResponseDto responseDto,
                                                                                             OfficeProjection officeProjection,
                                                                                             RoomResponseDto roomResponseDto,
                                                                                             StorePartAvailabilityProjection storePartAvailabilityProjection) {
        if (Objects.nonNull(roomResponseDto)) {
            responseDto.setRoomId(roomResponseDto.getRoomId());
            responseDto.setRoomCode(roomResponseDto.getRoomCode());
            responseDto.setOfficeId(roomResponseDto.getOfficeId());
            responseDto.setOfficeCode(roomResponseDto.getOfficeCode());
        } else {
            responseDto.setOfficeId(officeProjection.getId());
            responseDto.setOfficeCode(officeProjection.getCode());
        }
        responseDto.setAcTypeId(storePartAvailabilityProjection.getPartModelAircraftModelId());
        responseDto.setAcType(storePartAvailabilityProjection.getPartModelAircraftModelAircraftModelName());
        responseDto.setPartClassification(storePartAvailabilityProjection.getPartClassification());
        return responseDto;
    }

    public static StorePartAvailabilityResponseDto convertToStorePartAvailabilityResponseDto(StorePartAvailabilityResponseDto responseDto,
                                                                                             RoomResponseDto roomResponseDto,
                                                                                             RackResponseDto rackResponseDto,
                                                                                             StorePartAvailabilityProjection storePartAvailabilityProjection) {
        if (Objects.nonNull(rackResponseDto)) {
            responseDto.setOfficeId(rackResponseDto.getOfficeId());
            responseDto.setOfficeCode(rackResponseDto.getOfficeCode());
            responseDto.setRoomId(rackResponseDto.getRoomId());
            responseDto.setRoomCode(rackResponseDto.getRoomCode());
            responseDto.setRackId(rackResponseDto.getRackId());
            responseDto.setRackCode(rackResponseDto.getRackCode());
        } else {
            responseDto.setOfficeId(roomResponseDto.getOfficeId());
            responseDto.setOfficeCode(roomResponseDto.getOfficeCode());
            responseDto.setRoomId(roomResponseDto.getRoomId());
            responseDto.setRoomCode(roomResponseDto.getRoomCode());
        }
        responseDto.setAcTypeId(storePartAvailabilityProjection.getPartModelAircraftModelId());
        responseDto.setAcType(storePartAvailabilityProjection.getPartModelAircraftModelAircraftModelName());
        responseDto.setPartClassification(storePartAvailabilityProjection.getPartClassification());
        return responseDto;
    }

    public static StorePartAvailabilityResponseDto convertToStorePartAvailabilityResponseDto(StorePartAvailabilityResponseDto responseDto,
                                                                                             RackResponseDto rackResponseDto,
                                                                                             RackRowResponseDto rackRowResponseDto,
                                                                                             StorePartAvailabilityProjection storePartAvailabilityProjection) {
        if (Objects.nonNull(rackRowResponseDto)) {
            responseDto.setOfficeId(rackRowResponseDto.getOfficeId());
            responseDto.setOfficeCode(rackRowResponseDto.getOfficeCode());
            responseDto.setRoomId(rackRowResponseDto.getRoomId());
            responseDto.setRoomCode(rackRowResponseDto.getRoomCode());
            responseDto.setRackId(rackRowResponseDto.getRackId());
            responseDto.setRackCode(rackRowResponseDto.getRackCode());
            responseDto.setRackRowId(rackRowResponseDto.getRackRowId());
            responseDto.setRackRowCode(rackRowResponseDto.getRackRowCode());
            responseDto.setAcTypeId(storePartAvailabilityProjection.getPartModelAircraftModelId());
            responseDto.setAcType(storePartAvailabilityProjection.getPartModelAircraftModelAircraftModelName());
        } else {
            responseDto.setOfficeId(rackResponseDto.getOfficeId());
            responseDto.setOfficeCode(rackResponseDto.getOfficeCode());
            responseDto.setRoomId(rackResponseDto.getRoomId());
            responseDto.setRoomCode(rackResponseDto.getRoomCode());
            responseDto.setRackId(rackResponseDto.getRackId());
            responseDto.setRackCode(rackResponseDto.getRackCode());
            responseDto.setAcTypeId(storePartAvailabilityProjection.getPartModelAircraftModelId());
            responseDto.setAcType(storePartAvailabilityProjection.getPartModelAircraftModelAircraftModelName());
        }
        responseDto.setAcTypeId(storePartAvailabilityProjection.getPartModelAircraftModelId());
        responseDto.setAcType(storePartAvailabilityProjection.getPartModelAircraftModelAircraftModelName());
        responseDto.setPartClassification(storePartAvailabilityProjection.getPartClassification());
        return responseDto;
    }

    public static StorePartAvailabilityResponseDto convertToStorePartAvailabilityResponseDto(StorePartAvailabilityResponseDto responseDto,
                                                                                             RackRowResponseDto rackRowResponseDto,
                                                                                             RackRowBinResponseDto rackRowBinResponseDto,
                                                                                             StorePartAvailabilityProjection storePartAvailabilityProjection) {

        if (Objects.nonNull(rackRowBinResponseDto)) {
            responseDto.setOfficeId(rackRowBinResponseDto.getOfficeId());
            responseDto.setOfficeCode(rackRowBinResponseDto.getOfficeCode());
            responseDto.setRoomId(rackRowBinResponseDto.getRoomId());
            responseDto.setRoomCode(rackRowBinResponseDto.getRoomCode());
            responseDto.setRackId(rackRowBinResponseDto.getRackId());
            responseDto.setRackCode(rackRowBinResponseDto.getRackCode());
            responseDto.setRackRowId(rackRowBinResponseDto.getRackRowId());
            responseDto.setRackRowCode(rackRowBinResponseDto.getRackRowCode());
            responseDto.setRackRowBinId(rackRowBinResponseDto.getRackRowId());
            responseDto.setRackRowBinCode(rackRowBinResponseDto.getRackRowBinCode());

        } else {
            responseDto.setOfficeId(rackRowResponseDto.getOfficeId());
            responseDto.setOfficeCode(rackRowResponseDto.getOfficeCode());
            responseDto.setRoomId(rackRowResponseDto.getRoomId());
            responseDto.setRoomCode(rackRowResponseDto.getRoomCode());
            responseDto.setRackId(rackRowResponseDto.getRackId());
            responseDto.setRackCode(rackRowResponseDto.getRackCode());
        }
        responseDto.setAcTypeId(storePartAvailabilityProjection.getPartModelAircraftModelId());
        responseDto.setAcType(storePartAvailabilityProjection.getPartModelAircraftModelAircraftModelName());
        responseDto.setPartClassification(storePartAvailabilityProjection.getPartClassification());
        return responseDto;
    }
}
