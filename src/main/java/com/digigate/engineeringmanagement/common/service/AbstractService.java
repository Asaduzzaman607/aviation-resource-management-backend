package com.digigate.engineeringmanagement.common.service;


import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.payload.IDto;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.common.service.impl.RoleServiceImpl;
import com.digigate.engineeringmanagement.common.util.Helper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.FIRST_INDEX;

@RequiredArgsConstructor
public abstract class AbstractService<E extends AbstractDomainBasedEntity, D extends IDto>
    implements IService<E, D> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(RoleServiceImpl.class);
    private final AbstractRepository<E> repository;

    @Override
    public <T> T getSingle(Long id) {
        return convertToResponseDto(findByIdUnfiltered(id));
    }

    @Override
    public E findById(Long id) {
        return findOptionalById(id, true).orElseThrow(() -> buildException(ErrorId.NOT_FOUND_DYNAMIC,
            ErrorId.DATA_NOT_FOUND));
    }

    @Override
    public E findByIdUnfiltered(Long id) {
        return findOptionalById(id, false).orElseThrow(() -> buildException(ErrorId.NOT_FOUND_DYNAMIC,
            ErrorId.DATA_NOT_FOUND));
    }

    @Override
    public Optional<E> findOptionalById(Long id, boolean activeRequired) {
        if (ObjectUtils.isEmpty(id)) {
           throw buildException(ErrorId.ID_IS_REQUIRED_DYNAMIC, ErrorId.ID_IS_REQUIRED);
        }
        return activeRequired ? repository.findByIdAndIsActiveTrue(id) : repository.findById(id);
    }

    @Override
    public PageData getAll(Boolean isActive, Pageable pageable) {
        Page<E> pagedData = repository.findAllByIsActive(isActive, pageable);
        List<Object> models = pagedData.getContent().stream().map(this::convertToResponseDto)
            .collect(Collectors.toList());
        return PageData.builder()
                .model(models)
                .totalPages(pagedData.getTotalPages())
                .totalElements(pagedData.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    @Override
    public List<E> getAllByDomainIdIn(Set<Long> ids, Boolean isActive) {
        return repository.findAllByIdInAndIsActive(ids, isActive);
    }

    @Override
    public List<E> getAllByDomainIdInUnfiltered(Set<Long> ids) {
        return repository.findAllByIdIn(ids);
    }

    @Override
    public void updateActiveStatus(Long id, Boolean isActive) {
        E e = findByIdUnfiltered(id);
        if (e.getIsActive() == isActive) {
            throw EngineeringManagementServerException.badRequest(ErrorId.ONLY_TOGGLE_VALUE_ACCEPTED);
        }

        e.setIsActive(isActive);
        saveItem(e);
    }

    @Override
    public E create(D d) {
        validateClientData(d, null);
        E entity = convertToEntity(d);
        return saveItem(entity);
    }

    @Override
    public E update(D d, Long id) {
        validateClientData(d, id);
        final E entity = updateEntity(d, findByIdUnfiltered(id));
        return saveItem(entity);
    }

    @Override
    public E saveItem(E entity) {
        try {
            return repository.save(entity);
        } catch (Exception e) {
            String name = entity.getClass().getSimpleName();
            LOGGER.error("Save failed for entity {}", name);
            LOGGER.error("Error message: {}", e.getMessage());
            throw EngineeringManagementServerException.dataSaveException(Helper.createDynamicCode(ErrorId.DATA_NOT_SAVED_DYNAMIC,
                    name));
        }
    }

    /**
     * This method is responsible for batch save
     *
     * @param entityList {@link List<E>}
     * @return List<E>
     */
    @Override
    public List<E> saveItemList(List<E> entityList) {
        try {
            if (CollectionUtils.isEmpty(entityList)) {
                return entityList;
            }
            return repository.saveAll(entityList);
        } catch (Exception e) {
            String entityName = entityList.get(0).getClass().getSimpleName();
            LOGGER.error("Save failed for entity {}", entityName);
            LOGGER.error("Error message: {}", e.getMessage());
            throw EngineeringManagementServerException.dataSaveException(Helper.createDynamicCode(ErrorId.DATA_NOT_SAVED_DYNAMIC,
                    entityName));
        }
    }


    protected abstract <T> T convertToResponseDto(E e);
    protected abstract E convertToEntity(D d);
    protected abstract E updateEntity(D dto, E entity);


    private EngineeringManagementServerException buildException(String dynamicMessage, String staticMessage) {
        try {
            String typeName = ((Class) (((ParameterizedType) this.getClass().getGenericSuperclass())
                .getActualTypeArguments()[FIRST_INDEX])).getSimpleName();
            return EngineeringManagementServerException.notFound(Helper.createDynamicCode(dynamicMessage,
                typeName));
        } catch (Exception e) {
            LOGGER.error("--- Could not determine entity name ---");
            return EngineeringManagementServerException.notFound(staticMessage);
        }
    }
}
