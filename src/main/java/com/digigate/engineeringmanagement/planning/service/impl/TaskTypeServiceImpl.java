package com.digigate.engineeringmanagement.planning.service.impl;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.planning.payload.request.TaskTypeSearchDto;
import com.digigate.engineeringmanagement.planning.entity.TaskType;
import com.digigate.engineeringmanagement.planning.payload.request.TaskTypeDto;
import com.digigate.engineeringmanagement.planning.payload.response.TaskTypeModelView;
import com.digigate.engineeringmanagement.planning.repository.TaskTypeRepository;
import com.digigate.engineeringmanagement.planning.service.TaskTypeService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Task Service
 *
 * @author Asifur Rahman
 */
@Service
public class TaskTypeServiceImpl extends AbstractSearchService<TaskType, TaskTypeDto, TaskTypeSearchDto> implements TaskTypeService {

    private final TaskTypeRepository taskTypeRepository;

    private static final String NAME_FIELD = "name";
    private static final String IS_ACTIVE = "isActive";

    public TaskTypeServiceImpl(AbstractRepository<TaskType> repository, TaskTypeRepository taskTypeRepository) {
        super(repository);
        this.taskTypeRepository = taskTypeRepository;
    }

    /**
     * convert response  from entity
     *
     * @param taskType {@link TaskType}
     * @return {@link TaskTypeModelView}
     */

    @Override
    protected TaskTypeModelView convertToResponseDto(TaskType taskType) {
        return TaskTypeModelView
                .builder()
                .id(taskType.getId())
                .name(taskType.getName())
                .description(taskType.getDescription())
                .isActive(taskType.getIsActive())
                .build();
    }

    /**
     * convert entity  from dto
     *
     * @param taskTypeDto {@link TaskTypeDto}
     * @return {@link TaskType}
     */

    @Override
    protected TaskType convertToEntity(TaskTypeDto taskTypeDto) {
        return mapToEntity(taskTypeDto, new TaskType());
    }


    @Override
    protected TaskType updateEntity(TaskTypeDto dto, TaskType entity) {
        return mapToEntity(dto, entity);
    }

    private TaskType mapToEntity(TaskTypeDto dto, TaskType entity) {
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        return entity;
    }

    @Override
    public Boolean validateClientData(TaskTypeDto dto, Long id) {
        Optional<Long> optionalId = taskTypeRepository.findByName(dto.getName());
        if (optionalId.isPresent() && !optionalId.get().equals(id)) {
            throw EngineeringManagementServerException.badRequest(ErrorId.TASK_TYPE_NAME_ALREADY_EXIST);
        }
        return true;
    }

    @Override
    protected Specification<TaskType> buildSpecification(TaskTypeSearchDto searchDto) {
        CustomSpecification<TaskType> customSpecification = new CustomSpecification<>();
        return Specification.where(customSpecification.likeSpecificationAtRoot(searchDto.getName(), NAME_FIELD)
                .and(customSpecification.equalSpecificationAtRoot(searchDto.getIsActive(), IS_ACTIVE)));
    }

    @Override
    public List<TaskType> getAllActiveTaskTypes(Boolean isActive) {
        return taskTypeRepository.findAllActiveTaskTypes(isActive);
    }
}
