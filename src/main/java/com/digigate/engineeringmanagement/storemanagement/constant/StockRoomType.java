package com.digigate.engineeringmanagement.storemanagement.constant;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public enum StockRoomType {
    ISSUE_DEMAND_CONSUMABLE("Issue/Demand Consumable"),
    ISSUE_DEMAND_COMPONENT("Issue/Demand Component"),
    STORE_RETURN_CONSUMABLE("Store/Return Consumable"),
    STORE_RETURN_COMPONENT("Store/Return Component");

    private final String value;

    StockRoomType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue(){ return this.value;}
    private static final Map<StockRoomType, String> stringStockRoomTypeHashMap = new HashMap<>();

    static {
        for(StockRoomType stockRoomType : StockRoomType.values()){
            stringStockRoomTypeHashMap.put(stockRoomType, stockRoomType.getValue());
        }
    }

    @JsonCreator
    public static String customStockRoomType(StockRoomType stockRoomType){
        if(!stringStockRoomTypeHashMap.containsKey(stockRoomType)){
            throw new EngineeringManagementServerException(
                    ErrorId.DATA_NOT_FOUND,
                    HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID)
            );
        }
        return stringStockRoomTypeHashMap.get(stockRoomType);
    }
}
