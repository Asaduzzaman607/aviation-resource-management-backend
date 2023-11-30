package com.digigate.engineeringmanagement.common.controller;


import com.digigate.engineeringmanagement.common.entity.AbstractEntity;
import com.digigate.engineeringmanagement.common.payload.IDto;
import com.digigate.engineeringmanagement.common.payload.SDto;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface ISearchController<E extends AbstractEntity, D extends IDto, S extends SDto> extends IController<E, D> {
    ResponseEntity<PageData> search(S s, Pageable pageable);
}
