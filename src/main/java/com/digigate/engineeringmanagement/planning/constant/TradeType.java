package com.digigate.engineeringmanagement.planning.constant;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.collections4.map.HashedMap;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;

import java.util.Map;

public enum TradeType {
    B1(0),
    B2(1);

    private final Integer value;

    TradeType(Integer value) {
        this.value = value;
    }

    @JsonValue
    public Integer getValue() {
        return this.value;
    }

    private static final Map<Integer, TradeType> tradeTypeMap = new HashedMap<>();

    static {
        for (TradeType tradeType : TradeType.values()) {
            tradeTypeMap.put(tradeType.getValue(), tradeType);
        }
    }

    @JsonCreator
    public static TradeType create(Integer id) {
        if (!tradeTypeMap.containsKey(id)) {
            throw new EngineeringManagementServerException(
                    ErrorId.UNABLE_TO_PARSE,
                    HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID));
        }
        return tradeTypeMap.get(id);
    }


}
