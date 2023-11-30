package com.digigate.engineeringmanagement.common.service;


import com.digigate.engineeringmanagement.common.entity.AbstractEntity;
import com.digigate.engineeringmanagement.common.payload.IDto;
import com.digigate.engineeringmanagement.common.payload.SDto;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import org.springframework.data.domain.Pageable;

public interface ISearchService<E extends AbstractEntity, D extends IDto, S extends SDto> extends IService<E, D> {
    PageData search(S s, Pageable pageable);
}
