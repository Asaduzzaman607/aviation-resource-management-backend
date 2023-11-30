package com.digigate.engineeringmanagement.planning.service.impl;

import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.service.AbstractService;
import com.digigate.engineeringmanagement.common.util.DateUtil;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Aircraft;
import com.digigate.engineeringmanagement.configurationmanagement.service.aircraftinformation.AircraftService;
import com.digigate.engineeringmanagement.planning.entity.Defect;
import com.digigate.engineeringmanagement.planning.payload.request.DefectDto;
import com.digigate.engineeringmanagement.planning.payload.request.DefectSearchDto;
import com.digigate.engineeringmanagement.planning.payload.response.*;
import com.digigate.engineeringmanagement.planning.repository.DefectRepository;
import com.digigate.engineeringmanagement.planning.service.AircraftLocationService;
import com.digigate.engineeringmanagement.planning.service.DefectService;
import com.digigate.engineeringmanagement.planning.service.PartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Defect  Service Impl
 *
 * @author Asifur Rahman
 */
@Service
public class DefectServiceImpl extends AbstractService<Defect, DefectDto> implements DefectService {

    private final AircraftService aircraftService;
    private final PartService partService;
    private final AircraftLocationService aircraftLocationService;
    private final DefectRepository defectRepository;

    private static final String LOCATION_FIELD = "locationId";
    private static final String AIRCRAFT_FIELD = "aircraftId";
    private static final String PART_FIELD = "partId";
    private static final String DATE_FIELD = "date";

    private static final String IS_ACTIVE = "isActive";

    @Autowired
    public DefectServiceImpl(AircraftService aircraftService,
                             PartService partService, AircraftLocationService aircraftLocationService,
                             DefectRepository defectRepository) {
        super(defectRepository);
        this.aircraftService = aircraftService;
        this.partService = partService;
        this.aircraftLocationService = aircraftLocationService;
        this.defectRepository = defectRepository;
    }


    @Override
    protected DefectViewModel convertToResponseDto(Defect defect) {
        return DefectViewModel.builder()
                .id(defect.getId())
                .aircraftId(defect.getAircraftId())
                .aircraftName(defect.getAircraft().getAircraftName())
                .date(defect.getDate())
                .reference(defect.getReference().orElse(null))
                .defectType(defect.getDefectType().orElse(null))
                .partId(defect.getPartId().orElse(null))
                .partNumber((Objects.nonNull(defect.getPart())) ? defect.getPart().getPartNo() : null)
                .nomenclature((Objects.nonNull(defect.getPart())) ? defect.getPart().getDescription() : null)
                .locationId(defect.getLocationId().orElse(null))
                .location((Objects.nonNull(defect.getAircraftLocation())) ? defect.getAircraftLocation().getName() : null)
                .defectDesc(defect.getDefectDesc().orElse(null))
                .actionDesc(defect.getActionDesc().orElse(null))
                .isActive(defect.getIsActive())
                .build();
    }

    @Override
    protected Defect convertToEntity(DefectDto defectDto) {
        return mapToEntity(new Defect(), defectDto);
    }

    @Override
    protected Defect updateEntity(DefectDto dto, Defect entity) {
        return mapToEntity(entity, dto);
    }

    private Defect mapToEntity(Defect entity, DefectDto dto) {
        entity.setAircraft(aircraftService.findById(dto.getAircraftId()));
        entity.setReference(dto.getReference());
        entity.setActionDesc(dto.getActionDesc());
        entity.setDefectDesc(dto.getDefectDesc());
        entity.setDefectType(dto.getDefectType());
        entity.setDate(dto.getDate());
        entity.setAircraftLocation((Objects.nonNull(dto.getLocationId()) ?
                aircraftLocationService.findById(dto.getLocationId()) : null));
        entity.setPart((Objects.nonNull(dto.getPartId()) ? partService.findById(dto.getPartId()) : null));

        return entity;
    }

    private List<Defect> mapToEntityList(List<DefectDto> defectDtoList){

        List<Defect> defectList = new ArrayList<>();
        defectDtoList.forEach(dto->{
            defectList.add(convertToEntity(dto));
        });
        return defectList;
    }

    @Override
    public void createDefectBulk(List<DefectDto> defectDtoList){
        saveItemList(mapToEntityList(defectDtoList));
    }

    @Override
    public PageData searchDefects(DefectSearchDto searchDto, Pageable pageable){
        Page<DefectSearchViewModel> searchViewModels = defectRepository.searchDefects(searchDto.getAircraftId(),
                searchDto.getLocationId(), searchDto.getPartId(), searchDto.getFromDate(), searchDto.getToDate(),
                searchDto.getIsActive(), searchDto.getIsPageable() ? pageable : Pageable.unpaged());

        return PageData.builder()
                .model(searchViewModels.getContent())
                .totalPages(searchViewModels.getTotalPages())
                .totalElements(searchViewModels.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();

    }

    @Override
    public List<DefectDto> getGeneratedDefectList(List<DefRectSearchViewModel> generatedDefects, Long aircraftId){
        Aircraft aircraft = aircraftService.findById(aircraftId);
        List<DefectDto> defectDtoList = new ArrayList<>();
        generatedDefects.forEach(d->{
            defectDtoList.add(
                    DefectDto.builder()
                            .aircraftId(d.getAircraftId())
                            .aircraftName(aircraft.getAircraftName())
                            .defectId(d.getDefectId())
                            .ata(d.getAta())
                            .locationId(d.getLocationId())
                            .date(d.getAmlDate())
                            .partId(d.getPartId())
                            .partNo(d.getPartNo())
                            .defectDesc(d.getDefectDesc())
                            .reference(d.getReference())
                            .actionDesc(d.getActionDesc())
                            .build());
        });
        return defectDtoList;
    }

    @Override
    public PageData findTopAtaReport(DefectSearchDto searchDto, Pageable pageable){
        DateUtil.isValidateDateRangeWith12Months(searchDto.getFromDate(), searchDto.getToDate());
        Page<TopAtaViewModel> topAtaViewModelPage = defectRepository.findTopTenAta(searchDto.getAircraftId(),
                searchDto.getFromDate(), searchDto.getToDate(),
                searchDto.getIsPageable() ? pageable : Pageable.unpaged());

        return PageData.builder()
                .model(topAtaViewModelPage.getContent())
                .totalPages(topAtaViewModelPage.getTotalPages())
                .totalElements(topAtaViewModelPage.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    @Override
    public PageData findCrrReport(DefectSearchDto searchDto, Pageable pageable){

        DateUtil.isValidateDateRangeWith12Months(searchDto.getFromDate(), searchDto.getToDate());

        Page<CrrReportViewModel> crrReport = defectRepository.findCrrReport(searchDto.getAircraftModelId(),
                searchDto.getFromDate(), searchDto.getToDate(),
                searchDto.getIsPageable() ? pageable : Pageable.unpaged());

        return PageData.builder()
                .model(crrReport.getContent())
                .totalPages(crrReport.getTotalPages())
                .totalElements(crrReport.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

}
