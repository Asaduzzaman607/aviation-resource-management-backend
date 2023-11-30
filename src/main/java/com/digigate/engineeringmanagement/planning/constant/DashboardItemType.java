package com.digigate.engineeringmanagement.planning.constant;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum DashboardItemType {

    MEL(0, "MEL"),
    A_CHECK(1, "A"),
    C_CHECK(2, "C"),
    CHECK_2Y(3, "2Y"),
    CHECK_4Y(4, "4Y"),
    CHECK_8Y(4, "8Y");

    public final Integer id;
    public final String val;

    DashboardItemType(Integer id, String val) {
        this.id = id;
        this.val = val;
    }


    @JsonValue
    public String getVal() {
        return this.val;
    }


    private static final Map<String, DashboardItemType> dashboardItemTypeValMap = new HashMap<>();


    static {
        for (DashboardItemType d : DashboardItemType.values()) {
            dashboardItemTypeValMap.put(d.getVal(), d);
        }
    }


    public static DashboardItemType getVal(String val) {
        if (!dashboardItemTypeValMap.containsKey(val)) {
            return MEL;
        }
        return dashboardItemTypeValMap.get(val);
    }
}
