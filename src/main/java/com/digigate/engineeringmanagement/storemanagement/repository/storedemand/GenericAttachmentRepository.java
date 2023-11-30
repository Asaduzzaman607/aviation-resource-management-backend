package com.digigate.engineeringmanagement.storemanagement.repository.storedemand;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.storemanagement.constant.FeatureName;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.GenericAttachment;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface GenericAttachmentRepository extends AbstractRepository<GenericAttachment> {

    Set<GenericAttachment> findAllByFeatureNameAndRecordIdInAndIsActiveTrue(FeatureName featureName, Set<Long> recordIds);

    Set<GenericAttachment> findAllByFeatureNameAndRecordIdAndIsActiveTrue(FeatureName featureName, Long recordId);
}
