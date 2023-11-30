package com.digigate.engineeringmanagement.planning.service.impl;

import java.util.Optional;

/**
 * Interface of AmlBookService
 *
 * @author Pranoy Das
 */
public interface IAmlBookService {
    Optional<Integer> findAmlBookByAircraftAndPageNumber(Long aircraftId, Integer pageNumber);
}
