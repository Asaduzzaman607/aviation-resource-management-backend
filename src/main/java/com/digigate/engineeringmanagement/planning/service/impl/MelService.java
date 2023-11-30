package com.digigate.engineeringmanagement.planning.service.impl;

import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.service.AbstractService;
import com.digigate.engineeringmanagement.planning.entity.AMLDefectRectification;
import com.digigate.engineeringmanagement.planning.entity.Mel;
import com.digigate.engineeringmanagement.planning.payload.request.MelDto;
import com.digigate.engineeringmanagement.planning.payload.request.MelSearchDto;
import com.digigate.engineeringmanagement.planning.payload.response.*;
import com.digigate.engineeringmanagement.planning.repository.MelRepository;
import com.digigate.engineeringmanagement.planning.service.AmlDefectRectificationService;
import com.digigate.engineeringmanagement.planning.service.MelIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Mel Service
 *
 * @author Asifur Rahman
 */
@Service
public class MelService extends AbstractService<Mel, MelDto> implements MelIService {
    private final AmlDefectRectificationService defectRectificationService;
    private final MelRepository melRepository;

    @Autowired
    public MelService(MelRepository melRepository,
                      AmlDefectRectificationService defectRectificationService) {
        super(melRepository);
        this.defectRectificationService = defectRectificationService;
        this.melRepository = melRepository;
    }

    /**
     * convert response  from entity
     *
     * @param mel {@link Mel}
     * @return {@link MelModelView}
     */
    @Override
    protected MelModelView convertToResponseDto(Mel mel) {
        Optional<AmlPageNoData> pageNoDataOptional = melRepository.findMelDataByMelId(mel.getId());
        return MelModelView
                .builder()
                .id(mel.getId())
                .amlPage(pageNoDataOptional.orElse(null))
                .isActive(mel.getIsActive())
                .build();
    }


    /**
     * convert entity  from dto
     *
     * @param dto {@link MelDto}
     * @return {@link Mel}
     */

    @Override
    protected Mel convertToEntity(MelDto dto) {
        return mapToEntity(dto, new Mel());
    }

    @Override
    protected Mel updateEntity(MelDto dto, Mel entity) {
        return mapToEntity(dto, entity);
    }

    private Mel mapToEntity(MelDto dto, Mel entity) {
        AMLDefectRectification intermediateDefect = defectRectificationService.findById(dto.getIntDefRectId());
        AMLDefectRectification correctiveDefect =
                defectRectificationService.findByIdUnfiltered(dto.getCorrectDefRectId());

        entity.setIntDefRect(intermediateDefect);
        entity.setCorrectDefRect(correctiveDefect);
        return entity;
    }

    /**
     * Search Mel by aml date range
     *
     * @param melSearchDto {@link  MelSearchDto}
     * @return melReportModelViews {@link Page<MelReportModelView>}
     */
    @Override
    public PageData searchMelReport(MelSearchDto melSearchDto, Pageable pageable) {

        Page<MelReportModelView> melReportModelViews;
        if (Objects.equals(melSearchDto.getIsClosed(), true)) {
            melReportModelViews = melRepository.searchClosedMelByAmlDate(melSearchDto.getFromDate(),
                    melSearchDto.getToDate(), melSearchDto.getAircraftId(), melSearchDto.getIsPageable() ? pageable :
                            Pageable.unpaged());
        } else {
            melReportModelViews = melRepository.searchOpenMelByAmlDate(melSearchDto.getFromDate(),
                    melSearchDto.getToDate(), melSearchDto.getAircraftId(), melSearchDto.getIsPageable() ? pageable :
                            Pageable.unpaged());
        }

        return new PageData(
                melReportModelViews.getContent(),
                melReportModelViews.getTotalPages(),
                pageable.getPageNumber() + 1,
                melReportModelViews.getTotalElements());

    }

    @Override
    public List<MelViewMode> findAllUnclearedMel(Long aircraftId) {
        return melRepository.findAllUnclearedMel(aircraftId);
    }

    @Override
    public List<DueResponse> findOpenClosestMel(Long aircraftId) {
        Pageable pageable = PageRequest.of(0, 3);
        return melRepository.findOpenClosestMel(aircraftId, pageable).getContent();
    }
}
