package com.digigate.engineeringmanagement.planning.constant;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public enum LifeCodes {

    FLY_HOUR(1),
    FLY_CYCLE(2),
    CALENDER(3),
    APU_HOUR(4),
    APU_CYCLE(5);


    public final Integer val;

    LifeCodes(Integer val) {
        this.val = val;
    }

    public static String getName(Integer val) {
        for (LifeCodes codes : LifeCodes.values()) {
            if (Objects.equals(codes.val, val)) {
                return codes.name();
            }
        }
        return null;
    }

    public static LifeCodes getByName(String name){
        for(LifeCodes lifeCode : LifeCodes.values()){
            if(StringUtils.equals(lifeCode.name(), name)){
                return lifeCode;
            }
        }
        return null;
    }


}
