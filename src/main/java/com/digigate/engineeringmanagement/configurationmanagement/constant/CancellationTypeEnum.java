package com.digigate.engineeringmanagement.configurationmanagement.constant;

import io.swagger.models.auth.In;

/**
 * Cancellation Type Enum
 *
 * @author Nafiul Islam
 */
public enum CancellationTypeEnum {
    INITIAL_CANCELLATION(0),
    TOTAL_CANCELLATION(1);

    public final Integer cancellationType;

    CancellationTypeEnum(Integer cancellationType) {
        this.cancellationType = cancellationType;
    }

    public Integer getCancellationType() {
        return cancellationType;
    }
}
