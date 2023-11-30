package com.digigate.engineeringmanagement.common.util;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Set Utils
 *
 * @author Masud Rana
 */
public class SetUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(SetUtil.class);

    /**
     * Get set difference
     *
     * @param firstSet  {@link T}
     * @param secondSet {@link T}
     * @return {@link Set}
     */
    public static <T> Set<T> getSetDifference(Set<T> firstSet, Set<T> secondSet) {
        if (CollectionUtils.isEmpty(firstSet) && CollectionUtils.isEmpty(secondSet)) {
            return Collections.emptySet();
        }
        if (CollectionUtils.isEmpty(secondSet)) {
            return firstSet;
        }
        firstSet.removeIf(secondSet::contains);
        return firstSet;
    }

    /**
     * parse ids from list of object
     *
     * @param dataList {@link Set<T>}
     * @return {@link Set<Long>}
     */
    public static <T extends AbstractDomainBasedEntity> Set<Long> parseIds(Set<T> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return Collections.emptySet();
        }
        return dataList.stream().map(data -> data.getId()).collect(Collectors.toSet());
    }
}
