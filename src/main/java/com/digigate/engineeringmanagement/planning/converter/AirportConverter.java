package com.digigate.engineeringmanagement.planning.converter;

import com.digigate.engineeringmanagement.planning.dto.request.AirportDto;
import com.digigate.engineeringmanagement.planning.entity.Airport;

import java.util.Objects;

/**
 * Airport converter class
 *
 * @author ashiniSingha
 */
public class AirportConverter {
    /**
     * convert Airport Dto to Airport Entity
     *
     * @param airportDto                {@link  AirportDto}
     * @param airport                   {@link  Airport}
     * @return airport                  {@link  Airport}
     */
    public static Airport convertDtoToEntity(AirportDto airportDto, Airport airport){
        airport.setName(airportDto.getName());
        airport.setIataCode(airportDto.getIataCode());
        airport.setCountryCode(airportDto.getCountryCode());
        if(Objects.nonNull(airportDto.getIsActive()) ){
               airport.setIsActive(airportDto.getIsActive());
        }
        return airport;
    }
}
