package com.digigate.engineeringmanagement.planning.constant;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Aircraft Model Time Unit Type Enum
 *
 * @author Sayem Hasnat
 */
public enum AircraftModelTimeUnit {
    FH(1),
    DAYS(2);

    private final int value;

    AircraftModelTimeUnit(int value) {
        this.value = value;
    }

    @JsonValue
    public Integer getValue() {
        return this.value;
    }


    private static final Map<Integer, AircraftModelTimeUnit> aircraftModelTimeUnitMap = new HashMap<>();

    static {
        for (AircraftModelTimeUnit d : AircraftModelTimeUnit.values()) {
            aircraftModelTimeUnitMap.put(d.getValue(), d);
        }
    }

    @JsonCreator
    public static AircraftModelTimeUnit create(Integer id) {
        if (!aircraftModelTimeUnitMap.containsKey(id)) {
            throw EngineeringManagementServerException.badRequest(ErrorId.UNABLE_TO_PARSE_TIME_UNIT);


        }
        return aircraftModelTimeUnitMap.get(id);
    }
}
