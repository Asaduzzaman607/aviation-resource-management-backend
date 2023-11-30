package com.digigate.engineeringmanagement.common.util;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Map Utils
 *
 * @author Masud Rana
 */
public class MapUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(MapUtil.class);

    /**
     * convert map from list
     *
     * @param dataList {@link T}
     * @return {@link Map}
     */
    public static <T extends AbstractDomainBasedEntity> Map<Long, T> convertToMapById(List<T> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return Collections.emptyMap();
        }
        return dataList.stream().collect(Collectors.toMap(data -> data.getId(), Function.identity()));
    }

    /**
     * Get default value from map
     *
     * @param dataMap      {@link Map}
     * @param key          {@link K}
     * @param defaultValue {@link T}
     * @return {@link T}
     */
    public static <T, K> T getOrDefaultFromMap(Map<K, T> dataMap, K key, T defaultValue) {
        if (!dataMap.containsKey(key)) {
            return defaultValue;
        }
        T value = dataMap.get(key);
        return Objects.nonNull(value) ? value : defaultValue;
    }

    /**
     * Get default value from map
     *
     * @param dataMap {@link Map}
     * @param key     {@link K}
     * @return {@link Boolean}
     */
    public static <T, K> boolean containsNonNullValue(Map<K, T> dataMap, K key) {
        if (MapUtils.isEmpty(dataMap) || Objects.isNull(key)) {
            return false;
        }
        T value = dataMap.get(key);
        return Objects.nonNull(value);
    }

    /**
     * Get default value from map
     *
     * @param dataMap {@link Map}
     * @param key     {@link K}
     * @return {@link Boolean}
     */
    public static <T, K> T getOrElseThrow(Map<K, T> dataMap, K key, RuntimeException runtimeException) {
        if (!containsNonNullValue(dataMap, key)) {
            throw runtimeException;
        }
        return dataMap.get(key);
    }
}
