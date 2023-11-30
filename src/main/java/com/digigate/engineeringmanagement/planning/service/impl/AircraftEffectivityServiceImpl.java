package com.digigate.engineeringmanagement.planning.service.impl;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Aircraft;
import com.digigate.engineeringmanagement.planning.constant.EffectivityType;
import com.digigate.engineeringmanagement.planning.constant.TaskStatusEnum;
import com.digigate.engineeringmanagement.planning.dto.request.AircraftEffectivityDto;
import com.digigate.engineeringmanagement.planning.entity.AircraftEffectivity;
import com.digigate.engineeringmanagement.planning.entity.Task;
import com.digigate.engineeringmanagement.planning.payload.request.AircraftEffectivityTaskDto;
import com.digigate.engineeringmanagement.planning.repository.AircraftEffectivityRepository;
import com.digigate.engineeringmanagement.planning.service.AircraftEffectivityIService;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Aircraft Effectivity Service
 *
 * @author Sayem Hasnat
 */
@Service
public class AircraftEffectivityServiceImpl
        implements AircraftEffectivityIService {

    private final AircraftEffectivityRepository aircraftEffectivityRepository;


    /**
     * Autowired Constructor
     *
     * @param aircraftEffectivityRepository {@link AircraftEffectivityRepository}
     */
    @Autowired
    public AircraftEffectivityServiceImpl(AircraftEffectivityRepository aircraftEffectivityRepository) {
        this.aircraftEffectivityRepository = aircraftEffectivityRepository;
    }

    /**
     * This method will find Effectivity by aircraftId
     *
     * @param aircraftId {@link Long}
     * @return {@link List<AircraftEffectivityDto>}
     */
    @Override
    public List<AircraftEffectivityDto> getAircraftEffectivityByAircraft(Long aircraftId) {
        return aircraftEffectivityRepository.findByAircraftIdAndTaskStatus(aircraftId,
                EffectivityType.EFFECTIVE,
                TaskStatusEnum.CLOSED);
    }

    @Override
    public AircraftEffectivity getAircraftEffectiveByAircraftAndTask(Long aircraftId, Long taskId) {
        Optional<AircraftEffectivity> aircraftEffectivity = aircraftEffectivityRepository.findByAircraftIdAndTaskId(
                aircraftId, taskId);
        return aircraftEffectivity.orElseThrow(() ->
                new EngineeringManagementServerException(
                        ErrorId.AIRCRAFT_EFFECTIVITY_NOT_FOUND, HttpStatus.NOT_FOUND,
                        MDC.get(ApplicationConstant.TRACE_ID)));
    }

    @Override
    public List<AircraftEffectivityTaskDto> findAllAircraftEffectivityTaskDtoByAircraftId(Long aircraftId) {
        return aircraftEffectivityRepository.findAllByAircraft(aircraftId);
    }

    /**
     * This Method will save batch of aircraftEffectivity
     *
     * @param aircraftEffectivityList {@link List<AircraftEffectivity>}
     */
    @Override
    public void saveItemList(List<AircraftEffectivity> aircraftEffectivityList) {
        aircraftEffectivityRepository.saveAll(aircraftEffectivityList);
    }

    /**
     * @param aircraftEffectivityIds {@link AircraftEffectivity}
     */
    @Override
    public List<AircraftEffectivity> findAllAircraftEffectivityById(Set<Long> aircraftEffectivityIds) {
        return aircraftEffectivityRepository.findAllByIdIn(aircraftEffectivityIds);
    }

    /**
     * This method will convert aircraft effectivity request dto to entity
     *
     * @param requestDto          {@link AircraftEffectivityTaskDto}
     * @param aircraftEffectivity {@link AircraftEffectivity}
     * @param aircraft            {@link Aircraft}
     * @param taskMapById         {@link java.util.HashMap}
     * @return {@link AircraftEffectivity}
     */
    public AircraftEffectivity convertRequestDtoModelToEntity(
            AircraftEffectivityTaskDto requestDto,
            AircraftEffectivity aircraftEffectivity, Aircraft aircraft,
            Map<Long, Task> taskMapById) {
        aircraftEffectivity.setAircraft(aircraft);
        aircraftEffectivity.setTask(taskMapById.get(requestDto.getTaskId()));
        aircraftEffectivity.setEffectivityType(requestDto.getEffectivityType());
        aircraftEffectivity.setRemark(requestDto.getRemark());
        return aircraftEffectivity;
    }
}