package com.digigate.engineeringmanagement.common.service;


import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.common.entity.User;
import com.digigate.engineeringmanagement.common.payload.IDto;
import com.digigate.engineeringmanagement.common.payload.SDto;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.IS_ACTIVE_FIELD;


public abstract class AbstractSearchService<E extends AbstractDomainBasedEntity, D extends IDto, S extends SDto>
        extends AbstractService<E, D> implements ISearchService<E, D, S> {
    private final AbstractRepository<E> repository;

    public AbstractSearchService(AbstractRepository<E> repository) {
        super(repository);
        this.repository = repository;
    }

    /**
     * search entity by criteria
     *
     * @param searchDto {@link S}
     * @param pageable {@link Pageable}
     * @return {@link User}
     */
    @Override
    public PageData search(S searchDto, Pageable pageable) {
        Specification<E> propellerSpecification = buildSpecification(searchDto).and(new CustomSpecification<E>()
                .active(Objects.nonNull(searchDto.getIsActive()) ? searchDto.getIsActive() : true, IS_ACTIVE_FIELD));
        Page<E> pagedData;
        if (searchDto.getIsDesc().equals(true)) {
            Sort sort = pageable.getSort().descending();
            pagedData = repository.findAll(propellerSpecification, PageRequest.of(pageable.getPageNumber(),
                    pageable.getPageSize(), sort));
        } else {
            pagedData = repository.findAll(propellerSpecification, pageable);
        }
        List<Object> models = pagedData.getContent()
                .stream().map(this::convertToResponseDto).collect(Collectors.toList());
        return PageData.builder()
                .model(models)
                .totalPages(pagedData.getTotalPages())
                .totalElements(pagedData.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }
    protected abstract Specification<E> buildSpecification(S searchDto);
}
