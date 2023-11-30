package com.digigate.engineeringmanagement.planning.service;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Aircraft;
import com.digigate.engineeringmanagement.configurationmanagement.service.aircraftinformation.AircraftService;
import com.digigate.engineeringmanagement.planning.entity.AircraftCheck;
import com.digigate.engineeringmanagement.planning.entity.AircraftCheckIndex;
import com.digigate.engineeringmanagement.planning.entity.NrcControlList;
import com.digigate.engineeringmanagement.planning.entity.WorkOrder;
import com.digigate.engineeringmanagement.planning.payload.request.NrcControlListDto;
import com.digigate.engineeringmanagement.planning.payload.request.NrcControlListSearchDto;
import com.digigate.engineeringmanagement.planning.payload.response.AircraftCheckIndexIdAndCheckViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.NrcControlListViewModel;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Nrc ControlList Service
 *
 * @author ashinisingha
 */

@Service
public class NrcControlListService extends AbstractSearchService<NrcControlList, NrcControlListDto,
        NrcControlListSearchDto> {

    private final AircraftService aircraftService;
    private final AircraftCheckIndexService aircraftCheckIndexService;
    private final WorkOrderService workOrderService;

    private static final String AIRCRAFT_ID = "aircraftId";

    public NrcControlListService(AbstractRepository<NrcControlList> repository, AircraftService aircraftService,
                                 AircraftCheckIndexService aircraftCheckIndexService,
                                 WorkOrderService workOrderService) {
        super(repository);
        this.aircraftService = aircraftService;
        this.aircraftCheckIndexService = aircraftCheckIndexService;
        this.workOrderService = workOrderService;
    }

    @Override
    protected Specification<NrcControlList> buildSpecification(NrcControlListSearchDto searchDto) {
        CustomSpecification<NrcControlList> customSpecification = new CustomSpecification<>();
        return Specification.where(
          customSpecification.equalSpecificationAtRoot(searchDto.getAircraftId(), AIRCRAFT_ID )
        );
    }

    @Override
    protected NrcControlListViewModel convertToResponseDto(NrcControlList nrcControlList) {
        NrcControlListViewModel nrcControlListViewModel = new NrcControlListViewModel();
        nrcControlListViewModel.setId(nrcControlList.getId());
        nrcControlListViewModel.setAircraftId(nrcControlList.getAircraftId());
        nrcControlListViewModel.setAircraftName(nrcControlList.getAircraft().getAircraftName());
        nrcControlListViewModel.setAirframeSerial(nrcControlList.getAircraft().getAirframeSerial());

        nrcControlListViewModel.setAircraftCheckIndexId(nrcControlList.getAircraftCheckIndexId());
        if (Objects.nonNull(nrcControlList.getAircraftCheckIndexId())) {
            List<String> titles = new ArrayList<>();
            for (AircraftCheck aircraftCheck : nrcControlList.getAircraftCheckIndex().getAircraftTypeCheckSet()) {
                if(Objects.nonNull(aircraftCheck.getCheck())) {
                    titles.add(aircraftCheck.getCheck().getTitle());
                }
            }
            nrcControlListViewModel.setTypeOfCheckList(titles.stream().sorted().collect(Collectors.toList()));
        }

        nrcControlListViewModel.setAircraftModelName(
                nrcControlList.getAircraft().getAircraftModel().getAircraftModelName());

        nrcControlListViewModel.setWoId(nrcControlList.getWoId());
        if(Objects.nonNull(nrcControlList.getWoId())){
            nrcControlListViewModel.setWoNo(nrcControlList.getWorkOrder().getWoNo());
        }
        nrcControlListViewModel.setDate(nrcControlList.getDate());
        return nrcControlListViewModel;
    }

    @Override
    protected NrcControlList convertToEntity(NrcControlListDto nrcControlListDto) {
        return saveOrUpdate(nrcControlListDto, new NrcControlList());
    }

    @Override
    protected NrcControlList updateEntity(NrcControlListDto dto, NrcControlList entity) {
        return saveOrUpdate( dto, entity );
    }

    private NrcControlList saveOrUpdate(NrcControlListDto nrcControlListDto, NrcControlList nrcControlList){
        Aircraft aircraft = aircraftService.findById(nrcControlListDto.getAircraftId());
        AircraftCheckIndex aircraftCheckIndex = null;
        WorkOrder workOrder = null;
        if(Objects.nonNull(nrcControlListDto.getAircraftCheckIndexId())){
            aircraftCheckIndex = aircraftCheckIndexService.findById(nrcControlListDto.getAircraftCheckIndexId());
        }
        if(Objects.nonNull(nrcControlListDto.getWoId())){
            workOrder = workOrderService.findById(nrcControlListDto.getWoId());
        }
        nrcControlList.setAircraft(aircraft);
        nrcControlList.setAircraftCheckIndex(aircraftCheckIndex);
        nrcControlList.setWorkOrder(workOrder);
        nrcControlList.setDate(nrcControlListDto.getDate());

        return nrcControlList;
    }

    public List<AircraftCheckIndexIdAndCheckViewModel> getAircraftCheckIndexListByAircraftId(Long aircraftId){
        return aircraftCheckIndexService.getAcCheckIndexByAircraftId(aircraftId);
    }

}
