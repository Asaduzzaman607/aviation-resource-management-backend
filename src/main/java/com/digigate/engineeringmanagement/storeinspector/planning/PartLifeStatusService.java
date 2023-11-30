package com.digigate.engineeringmanagement.storeinspector.planning;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.storeinspector.payload.projection.PlanningSiProjection;
import com.digigate.engineeringmanagement.storeinspector.payload.response.storeinspector.PartLifeStatusResponseDto;
import com.digigate.engineeringmanagement.storeinspector.service.storeinspector.StoreInspectionService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class PartLifeStatusService {
    private final StoreInspectionService storeInspectionService;
    private final String OVER_HAUL = "O/H";
    private final String SHOP_CHECK = "SHP CK";

    public PartLifeStatusService(StoreInspectionService storeInspectionService) {
        this.storeInspectionService = storeInspectionService;
    }

    public PartLifeStatusResponseDto getPartLifeStatus(Long partId, Long serialId){
        Optional<PlanningSiProjection> planningSiProjection =
                storeInspectionService.getInspectionByPartIdAndSerialId(partId, serialId);
        return populateToResponse(planningSiProjection);
    }

    private PartLifeStatusResponseDto populateToResponse(Optional<PlanningSiProjection> planningSiProjection) {
        PartLifeStatusResponseDto partLifeStatusResponseDto = new PartLifeStatusResponseDto();
        partLifeStatusResponseDto.setIsPresent(planningSiProjection.isPresent());
        if(planningSiProjection.isPresent()){
            PlanningSiProjection partLifStatus = planningSiProjection.get();
            partLifeStatusResponseDto.setTsn(partLifStatus.getReturnPartsDetailTsn());
            partLifeStatusResponseDto.setCsn(partLifStatus.getReturnPartsDetailCsn());
            partLifeStatusResponseDto.setTso(partLifStatus.getReturnPartsDetailTso());
            partLifeStatusResponseDto.setCso(partLifStatus.getReturnPartsDetailCso());
            partLifeStatusResponseDto.setTsr(partLifStatus.getReturnPartsDetailTsr());
            partLifeStatusResponseDto.setCsr(partLifStatus.getReturnPartsDetailCsr());
            setInspectionStatus(partLifeStatusResponseDto, partLifStatus);
        }
        return partLifeStatusResponseDto;
    }

    private void setInspectionStatus(PartLifeStatusResponseDto partLifeStatusResponseDto, PlanningSiProjection partLifStatus) {
        List<String> inspectionStatus = Arrays.asList(partLifStatus.getPartStateName().split(ApplicationConstant.COMMA_SEPARATOR));
        inspectionStatus.forEach(status ->{
            partLifeStatusResponseDto.setIsOverHaul(status.equals(OVER_HAUL));
            partLifeStatusResponseDto.setIsShopCheck(status.equals(SHOP_CHECK));
        });
    }
}
