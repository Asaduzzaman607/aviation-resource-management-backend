package com.digigate.engineeringmanagement.common.specification;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import java.util.Objects;
import java.util.Set;

public class CustomSpecification<T> {
    public CustomSpecification() {

    }

    /**
     * get specification
     *
     * @param value      {@link Object}
     * @param columnName {@link String}
     * @return {Specification<T>}
     */
    public <E> Specification<T> equalSpecificationAtRoot(E value, String columnName) {
        return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            if (Objects.nonNull(value)) {
                if (value instanceof String) {
                    if (StringUtils.isBlank((String) value)) {
                        return null;
                    }
                }
                return criteriaBuilder.equal(root.get(columnName), value);
            }
            return null;
        };
    }

    public <E> Specification<T> notEqualSpecificationAtRoot(E value, String columnName) {
        return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            if (Objects.nonNull(value)) {
                if (value instanceof String) {
                    if (StringUtils.isBlank((String) value)) {
                        return null;
                    }
                }
                return criteriaBuilder.notEqual(root.get(columnName), value);
            }
            return null;
        };
    }

    public <E> Specification<T> active(boolean value, String columnName) {
        return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) ->
                criteriaBuilder.equal(root.get(columnName), value);
    }

    /**
     * back and forth search specification
     *
     * @param value      {@link String}
     * @param columnName {@link String}
     * @return {Specification<T>}
     */
    public Specification<T> likeSpecificationAtRoot(String value, String columnName) {
        return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            if (StringUtils.isNotBlank(value)) {
                return criteriaBuilder.like(root.get(columnName), value + "%");
            }
            return null;
        };
    }

    public Specification<T> likeSpecificationAtPrefixAndSuffix(String value, String columnName) {
        return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            if (StringUtils.isNotBlank(value)) {
                return criteriaBuilder.like(root.get(columnName), "%" + value + "%");
            }
            return null;
        };
    }

    /**
     * get specification
     *
     * @param value      {@link String}
     * @param columnName {@link String}
     * @return {Specification<T>}
     */
    public Specification<T> wildCardSpecificationAtRoot(String value, String columnName) {
        return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            if (StringUtils.isNotBlank(value)) {
                return criteriaBuilder.like(root.get(columnName), "%" + value + "%");
            }
            return null;
        };
    }

    /**
     * get specification
     *
     * @param values     {@link Set <E>}
     * @param columnName {@link String}
     * @return {Specification<T>}
     */
    public <E> Specification<T> inSpecificationAtRoot(Set<E> values, String columnName) {
        return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            if (CollectionUtils.isNotEmpty(values)) {
                query.distinct(true);
                return root.get(columnName).in(values);
            }
            return null;
        };
    }


    /**
     * get specification
     *
     * @param values            {@link Set <E>}
     * @param childEntityName   {@link String}
     * @param childEntityColumn {@link String}
     * @return {Specification<T>}
     */
    public <E> Specification<T> inSpecificationAtChild(Set<E> values,
                                                       String childEntityName, String childEntityColumn) {
        return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            if (CollectionUtils.isNotEmpty(values)) {
                query.distinct(true);
                return root.join(childEntityName).get(childEntityColumn).in(values);
            }
            return null;
        };
    }

    /**
     * get specification
     *
     * @param startValue {@link E}
     * @param endValue   {@link E}
     * @param columnName {@link String}
     * @return {Specification<T>}
     */
    public <E extends Comparable> Specification<T> inBetweenSpecification(
            E startValue, E endValue, String columnName) {
        return (root, query, criteriaBuilder) -> {
            if (Objects.isNull(startValue) || Objects.isNull(endValue)) {
                return null;
            }
            return criteriaBuilder.between(root.get(columnName), startValue, endValue);
        };
    }

    /**
     * get specification
     *
     * @param maxValue   {@link E}
     * @param columnName {@link String}
     * @return {Specification<T>}
     */
    public <E extends Comparable> Specification<T> lessThanOrEqualToSpecification(E maxValue, String columnName) {
        return (root, query, criteriaBuilder) -> {
            if (Objects.isNull(maxValue)) {
                return null;
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get(columnName), maxValue);
        };
    }

    /**
     * get specification
     *
     * @param minValue   {@link E}
     * @param columnName {@link String}
     * @return {Specification<T>}
     */
    public <E extends Comparable> Specification<T> greaterThanOrEqualToSpecification(E minValue, String columnName) {
        return (root, query, criteriaBuilder) -> {
            if (Objects.isNull(minValue)) {
                return null;
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get(columnName), minValue);
        };
    }

    /**
     * get equal match from join with child table
     *
     * @param value             {@link E}
     * @param childEntityName   {@link String}
     * @param childEntityColumn {@link String}
     * @return {Specification<T>}
     */
    public <E> Specification<T> equalSpecificationAtChild(E value,
                                                          String childEntityName, String childEntityColumn) {
        return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            if (Objects.nonNull(value)) {
                return criteriaBuilder.equal(root.join(childEntityName).get(childEntityColumn), value);
            }
            return null;
        };
    }


    /**
     * get specification
     *
     * @param value             {@link E}
     * @param childEntityName   {@link String}
     * @param childEntityColumn {@link String}
     * @return {Specification<T>}
     */
    public <E> Specification<T> likeSpecificationAtChild(E value,
                                                         String childEntityName, String childEntityColumn) {
        return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            if (Objects.nonNull(value)) {
                return criteriaBuilder.like(root.join(childEntityName).get(childEntityColumn), value + "%");
            }
            return null;
        };
    }

    public <E> Specification<T> likeAllSpecificationAtChild(E value,
                                                            String childEntityName, String childEntityColumn) {
        return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            if (Objects.nonNull(value)) {
                return criteriaBuilder.like(root.join(childEntityName).get(childEntityColumn), "%" + value + "%");
            }
            return null;
        };
    }

    public <E> Specification<T> likeSpecificationAtSecondLayerChild(E value,
                                                                   String firstLayerEntity, String secondLayerEntity,
                                                                    String secondLayerEntityColumn) {
        return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            if (Objects.nonNull(value)) {
                return criteriaBuilder.like(root.join(firstLayerEntity).join(secondLayerEntity).get(secondLayerEntityColumn), value + "%");
            }
            return null;
        };
    }

    public <E> Specification<T> likeSpecificationAtThirdLayerChild(E value,
                                                                   String firstLayerEntity, String secondLayerEntity,
                                                                   String thirdLayerEntity, String thirdLayerEntityColumn) {
        return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            if (Objects.nonNull(value)) {
                return criteriaBuilder.like(root.join(firstLayerEntity).join(secondLayerEntity).join(thirdLayerEntity).get(thirdLayerEntityColumn), value + "%");
            }
            return null;
        };
    }

    public <E> Specification<T> likeSpecificationAtFourthEntityChild(E value, String firstLayerEntity, String secondLayerEntity,
                                                                     String thirdLayerEntity, String fourthLayerEntity,
                                                                     String fourthLayerEntityColumn) {
        return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            if (Objects.nonNull(value)) {
                return criteriaBuilder.like(root.join(firstLayerEntity).join(secondLayerEntity).join(thirdLayerEntity)
                        .join(fourthLayerEntity).get(fourthLayerEntityColumn), value + "%");
            }
            return null;
        };
    }

    public <E> Specification<T> likeSpecificationAtFourthEntityChildAndFourthEntityJoinTypeLeft(E value, String firstLayerEntity,
                                                                                                String secondLayerEntity, String thirdLayerEntity,
                                                                                                String fourthLayerEntity, String fourthLayerEntityColumn) {
        return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            if (Objects.nonNull(value)) {
                return criteriaBuilder.like(root.join(firstLayerEntity).join(secondLayerEntity).join(thirdLayerEntity)
                        .join(fourthLayerEntity, JoinType.LEFT).get(fourthLayerEntityColumn), value + "%");
            }
            return null;
        };
    }
}
