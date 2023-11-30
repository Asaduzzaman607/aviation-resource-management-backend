package com.digigate.engineeringmanagement.planning.repository;

import com.digigate.engineeringmanagement.planning.payload.response.AmlDetailsResponseDto;

public interface AmlDetailsService {

    AmlDetailsResponseDto findAmlDetailsResponseById(Long id);
}
