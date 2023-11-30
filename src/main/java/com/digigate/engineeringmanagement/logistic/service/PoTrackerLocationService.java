package com.digigate.engineeringmanagement.logistic.service;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.logistic.entity.PoTracker;
import com.digigate.engineeringmanagement.logistic.entity.PoTrackerLocation;
import com.digigate.engineeringmanagement.logistic.payload.request.PoTrackerLocationRequestDto;
import com.digigate.engineeringmanagement.logistic.payload.response.PoTrackerLocationResponseDto;
import com.digigate.engineeringmanagement.logistic.repository.PoTrackerLocationRepository;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PoTrackerLocationService extends AbstractSearchService<PoTrackerLocation, PoTrackerLocationRequestDto, IdQuerySearchDto> {
    private final PoTrackerLocationRepository poTrackerLocationRepository;

    public PoTrackerLocationService(AbstractRepository<PoTrackerLocation> repository, PoTrackerLocationRepository poTrackerLocationRepository) {
        super(repository);
        this.poTrackerLocationRepository = poTrackerLocationRepository;
    }

    public List<PoTrackerLocation> findByPoTrackerIdIn(Set<Long> ids) {
        return poTrackerLocationRepository.findByPoTrackerIdInAndIsActiveTrue(ids);
    }

    public void saveAll(List<PoTrackerLocationRequestDto> poTrackerLocationRequestDto, PoTracker poTracker) {
        List<PoTrackerLocation> poTrackerLocationList = poTrackerLocationRequestDto.stream().map(poTrackerLocation ->
                convertToSaveEntity(poTrackerLocation, poTracker)).collect(Collectors.toList());
        super.saveItemList(poTrackerLocationList);
    }

    public void updateAll(List<PoTrackerLocationRequestDto> poTrackerLocationList, PoTracker poTracker) {
        Set<Long> updateIds = poTrackerLocationList.stream().map(PoTrackerLocationRequestDto::getId)
                .filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, PoTrackerLocation> poTrackerLocationMap = getAllByDomainIdInUnfiltered(updateIds).stream()
                .collect(Collectors.toMap(PoTrackerLocation::getId, Function.identity()));

        List<PoTrackerLocation> locationList = poTrackerLocationList.stream().map(p ->
                        convertToUpdateEntity(p, poTrackerLocationMap.getOrDefault(p.getId(),
                                new PoTrackerLocation()), poTracker))
                .collect(Collectors.toList());
        super.saveItemList(locationList);
    }

    @Override
    protected Specification<PoTrackerLocation> buildSpecification(IdQuerySearchDto searchDto) {
        return null;
    }

    public List<PoTrackerLocationResponseDto> convertToResponse(List<PoTrackerLocation> locationList) {
        return locationList.stream().map(this::convertToResponseDto).collect(Collectors.toList());
    }


    @Override
    protected PoTrackerLocationResponseDto convertToResponseDto(PoTrackerLocation poTrackerLocation) {
        return PoTrackerLocationResponseDto.builder()
                .id(poTrackerLocation.getId())
                .location(poTrackerLocation.getLocation())
                .trackerId(poTrackerLocation.getTrackerId())
                .date(poTrackerLocation.getDate())
                .awbNo(poTrackerLocation.getAwbNo())
                .build();
    }

    @Override
    protected PoTrackerLocation convertToEntity(PoTrackerLocationRequestDto poTrackerLocationRequestDto) {
        return null;
    }

    @Override
    protected PoTrackerLocation updateEntity(PoTrackerLocationRequestDto dto, PoTrackerLocation entity) {
        return null;
    }

    private PoTrackerLocation convertToSaveEntity(PoTrackerLocationRequestDto poTrackerLocationRequestDto, PoTracker poTracker) {
        PoTrackerLocation poTrackerLocation = new PoTrackerLocation();
        poTrackerLocation.setLocation(poTrackerLocationRequestDto.getLocation());
        poTrackerLocation.setPoTracker(poTracker);
        poTrackerLocation.setDate(poTrackerLocationRequestDto.getDate());
        poTrackerLocation.setAwbNo(poTrackerLocationRequestDto.getAwbNo());
        return poTrackerLocation;
    }

    private PoTrackerLocation convertToUpdateEntity(PoTrackerLocationRequestDto dto, PoTrackerLocation poTrackerLocation, PoTracker poTracker) {
        poTrackerLocation.setPoTracker(poTracker);
        poTrackerLocation.setLocation(dto.getLocation());
        poTrackerLocation.setDate(dto.getDate());
        poTrackerLocation.setAwbNo(dto.getAwbNo());
        return poTrackerLocation;
    }
}
