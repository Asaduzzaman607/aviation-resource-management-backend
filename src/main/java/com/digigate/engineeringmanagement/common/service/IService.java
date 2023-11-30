package com.digigate.engineeringmanagement.common.service;


import com.digigate.engineeringmanagement.common.entity.AbstractEntity;
import com.digigate.engineeringmanagement.common.payload.IDto;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface IService<E extends AbstractEntity, D extends IDto> {

    <T> T getSingle(Long id);

    E findById(Long id);

    E findByIdUnfiltered(Long id);

    Optional<E> findOptionalById(Long id, boolean activeRequired);

    PageData getAll(Boolean isActive, Pageable pageable);
    List<E> getAllByDomainIdIn(Set<Long> ids, Boolean isActive);
    E create(D d);
    E update(D d, Long id);

    List<E> getAllByDomainIdInUnfiltered(Set<Long> ids);

    void updateActiveStatus(Long id, Boolean b);

    E saveItem(E entity);

    List<E> saveItemList(List<E> entityList);

    default Boolean validateClientData(D d, Long id) {
        return Boolean.TRUE;
    }
}
