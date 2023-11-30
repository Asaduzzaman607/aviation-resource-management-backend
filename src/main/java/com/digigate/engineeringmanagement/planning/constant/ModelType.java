package com.digigate.engineeringmanagement.planning.constant;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public enum ModelType {
    AF_TCI(0, "AF TCI"),
    COMPONENT(1, "COMPONENT"),
    ENGINE(2, "ENGINE"),
    ENGINE_LLP(3, "ENGINE LLP"),
    ENGINE_LRU(4, "ENGINE LRU"),
    ENGINE_TCI(5, "ENGINE TCI"),
    MLG_LLP(6, "MLG LLP"),
    NLG(7, "NLG"),
    MLG(8, "MLG"),
    NLG_LLP(9, "NLG LLP"),
    PROPELLER(10, "PROPELLER"),
    PROPELLER_TCI(11, "PROPELLER TCI"),
    AF_LLP(12, "AF LLP"),
    APU_LLP(13, "APU LLP"),
    APU_LRU(14, "APU LRU"),
    APU_TCI(15, "APU TCI"),
    ENGINE_TMM(16, "ENGINE TMM"),
    ENGINE_RGB(17, "ENGINE RGB"),
    APU(18,"APU"),

    CONSUMABLE_MODEL(19, "CONSUMABLE_MODEL");

    private final Integer id;
    private final String name;

    ModelType(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    @JsonValue
    public Integer getId() {
        return this.id;
    }

    public static String getName(Integer id) {
        for (ModelType modelType : ModelType.values()) {
            if (Objects.equals(modelType.id, id)) {
                return modelType.name;
            }
        }
        return null;
    }

    private static final Map<Integer, ModelType> modelTypeMap = new HashMap<>();

    static {
        for (ModelType d : ModelType.values()) {
            modelTypeMap.put(d.getId(), d);
        }
    }

    @JsonCreator
    public static ModelType create(Integer id) {
        if (!modelTypeMap.containsKey(id)) {
            throw new EngineeringManagementServerException(
                    ErrorId.DATA_NOT_FOUND,
                    HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID));
        }
        return modelTypeMap.get(id);
    }

    public static ModelType getByName(String name) {
        for (ModelType modelType : ModelType.values()) {
            if (StringUtils.equals(modelType.name, name)) {
                return modelType;
            }
        }
        return null;
    }

    public static boolean isLLP(ModelType modelType) {
        return getLlpModelTypes().contains(modelType);
    }


    public static EnumSet<ModelType> getAllHardTimeModelTypes() {
        return EnumSet.of(
                AF_TCI,
                ENGINE_TCI,
                NLG,
                MLG,
                PROPELLER_TCI);
    }

    public static EnumSet<ModelType> getOccmModelTypes() {
        return EnumSet.of(
                ENGINE,
                ENGINE_LRU,
                PROPELLER,
                COMPONENT,
                APU_LRU,
                ENGINE_TMM,
                ENGINE_RGB);
    }

    public static EnumSet<ModelType> getLlpModelTypes() {
        return EnumSet.of(
                ENGINE_LLP,
                MLG,
                MLG_LLP,
                NLG,
                NLG_LLP,
                APU_LLP,
                AF_LLP);
    }

    public static EnumSet<ModelType> getAirframeApplianceAdModelTypes() {
        return EnumSet.of(
                COMPONENT,
                MLG_LLP,
                NLG_LLP,
                AF_TCI,
                AF_LLP,
                MLG,
                NLG);
    }

    public static EnumSet<ModelType> getEngineModelTypes() {
        return EnumSet.of(
                ENGINE,
                ENGINE_LLP,
                ENGINE_LRU,
                ENGINE_RGB,
                ENGINE_TCI,
                ENGINE_TMM);
    }

    public static EnumSet<ModelType> getNlgModelTypes(){
        return EnumSet.of(
                NLG,
                NLG_LLP
        );
    }


    public static EnumSet<ModelType> getEngineLandingGearApu(){
        return EnumSet.of(
                ENGINE,
                NLG,
                MLG,
                APU
        );
    }
}
