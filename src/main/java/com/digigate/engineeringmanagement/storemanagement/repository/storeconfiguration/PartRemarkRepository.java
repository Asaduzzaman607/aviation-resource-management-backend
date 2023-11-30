package com.digigate.engineeringmanagement.storemanagement.repository.storeconfiguration;

import com.digigate.engineeringmanagement.storemanagement.constant.RemarkType;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.PartRemark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PartRemarkRepository extends JpaRepository<PartRemark, Long> {
    Set<PartRemark> findByItemIdInAndRemarkTypeAndParentIdIn(Set<Long> ids, RemarkType remarkType, Set<Long> parentIds);

    void deleteByItemIdInAndParentIdAndRemarkType(Set<Long> ids, Long parentId, RemarkType remarkType);

    void deleteByParentIdAndRemarkType(Long parentId, RemarkType remarkType);

    void deleteByParentIdAndRemarkTypeIn(Long parentId, List<RemarkType> remarkType);

    Optional<PartRemark> findByItemIdAndRemarkTypeAndParentId(Long id, RemarkType storeIssue, Long id1);

    List<PartRemark> findByParentIdInAndRemarkType(Set<Long> id, RemarkType remarkType);
}
