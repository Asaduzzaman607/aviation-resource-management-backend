package com.digigate.engineeringmanagement.procurementmanagement.util;

import org.springframework.stereotype.Component;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.*;

@Component
public class CommonUtil {

    public boolean isInvisible(String key){
        return key.substring(VALUE_ZERO, VALUE_NINE)
                .equals(INVISIBLE.substring(VALUE_ZERO, VALUE_NINE));
    }
}
