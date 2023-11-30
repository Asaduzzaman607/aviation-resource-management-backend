package com.digigate.engineeringmanagement.planning.constant;

/**
 * Interval Type Enum
 *
 * @author Pranoy Das
 */
public enum IntervalTypeEnum {
    THRESHOLD(0),
    INTERVAL(1);

    private Integer intervalType;

    IntervalTypeEnum(int intervalType) {
        this.intervalType = intervalType;
    }

    public Integer getIntervalType() {
        return intervalType;
    }
}
