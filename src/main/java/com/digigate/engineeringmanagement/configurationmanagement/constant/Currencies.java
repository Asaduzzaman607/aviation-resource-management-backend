package com.digigate.engineeringmanagement.configurationmanagement.constant;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.HashMap;
import java.util.Map;

public enum Currencies {
    BDT,
    EUR,
    USD,
    RUPEE,
    GBP,
    SGD;

    private static final Map<Currencies, String> lookup = new HashMap<>();

    static {
        for (Currencies d : Currencies.values()) {
            lookup.put(d, d.name());
        }
    }

    @JsonCreator
    public static String forValue(Currencies currencies) {
        return lookup.get(currencies);
    }
}
