package com.digigate.engineeringmanagement.configurationmanagement.constant;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;

import java.util.EnumSet;
import java.util.Map;

/**
 * Classification Type Enum
 *
 * @author Nafiul Islam
 */
public enum ClassificationTypeEnum {

    TAKE_OFF_ABANDONED(0),
    RETURNS_BEFORE_TAKE_OFF(1),
    RETURNS_AFTER_TAKE_OFF(2),
    ENGINE_SHUT_DOWN_IN_FLIGHT(3),
    FIRE_WARNING_LIGHT(4),
    FUEL_DUMPING(5),
    OTHER_REPORTABLE_DEFECT(6),
    TURBULENCE(7),
    LIGHTNING_STRIKE(8),
    BIRD_STRIKE_JACKAL_HIT(9),
    FOREIGN_OBJECT_DAMAGE(10),
    AC_DAMAGED_BY_GROUND_EQPT(11),
    OTHER(12);

    private final Integer classificationType;

    ClassificationTypeEnum(Integer classificationType) {
        this.classificationType = classificationType;
    }

    @JsonValue
    public Integer getClassificationType() {
        return this.classificationType;
    }

    private static final Map<Integer, ClassificationTypeEnum> classificationTypeEnumHashedMap = new HashedMap<>();

    static {
        for(ClassificationTypeEnum classificationTypeEnum: ClassificationTypeEnum.values()){
            classificationTypeEnumHashedMap.put(classificationTypeEnum.getClassificationType(), classificationTypeEnum);
        }
    }

    @JsonCreator
    public static ClassificationTypeEnum create(Integer id){
        if(!classificationTypeEnumHashedMap.containsKey(id)){
            throw  EngineeringManagementServerException.badRequest(ErrorId.CLASSIFICATION_TYPE_ENUM_ERROR);
        }
        return classificationTypeEnumHashedMap.get(id);
    }

    public static ClassificationTypeEnum getByName(String name){
        for(ClassificationTypeEnum classificationTypeEnum: ClassificationTypeEnum.values()){
            if(StringUtils.equals(classificationTypeEnum.name(), name)){
                return classificationTypeEnum;
            }
        }
        return null;
    }

    public static EnumSet<ClassificationTypeEnum> technicalEnumSet = EnumSet.of(TAKE_OFF_ABANDONED,RETURNS_BEFORE_TAKE_OFF,
            RETURNS_AFTER_TAKE_OFF,ENGINE_SHUT_DOWN_IN_FLIGHT,FIRE_WARNING_LIGHT,FUEL_DUMPING,OTHER_REPORTABLE_DEFECT);

    public static EnumSet<ClassificationTypeEnum> nonTechnicalEnumSet = EnumSet.of(TURBULENCE,LIGHTNING_STRIKE,
            BIRD_STRIKE_JACKAL_HIT,FOREIGN_OBJECT_DAMAGE,AC_DAMAGED_BY_GROUND_EQPT,OTHER);
}
