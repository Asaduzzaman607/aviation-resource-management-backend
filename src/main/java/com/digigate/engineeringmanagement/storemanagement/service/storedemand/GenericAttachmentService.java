package com.digigate.engineeringmanagement.storemanagement.service.storedemand;

import com.digigate.engineeringmanagement.storemanagement.constant.FeatureName;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.GenericAttachment;
import com.digigate.engineeringmanagement.storemanagement.repository.storedemand.GenericAttachmentRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GenericAttachmentService {

    private final GenericAttachmentRepository genericAttachmentRepository;

    public GenericAttachmentService(GenericAttachmentRepository genericAttachmentRepository) {
        this.genericAttachmentRepository = genericAttachmentRepository;
    }

    public Set<String> getLinksByFeatureNameAndId(FeatureName featureName, Long recordId) {
        return genericAttachmentRepository.findAllByFeatureNameAndRecordIdAndIsActiveTrue(featureName, recordId)
                .stream().map(GenericAttachment::getLink).collect(Collectors.toSet());
    }

    public Set<String> getLinksByFeatureNameAndId(FeatureName featureName, Set<Long> recordIds) {
        return getAllAttachmentByFeatureNameAndId(featureName, recordIds).stream()
                .map(GenericAttachment::getLink).collect(Collectors.toSet());
    }

    public Set<GenericAttachment> getAllAttachmentByFeatureNameAndId(FeatureName featureName, Set<Long> recordIds) {
        return genericAttachmentRepository.findAllByFeatureNameAndRecordIdInAndIsActiveTrue(featureName, recordIds);
    }

    public Set<GenericAttachment> getAllAttachmentByFeatureNameAndId(FeatureName featureName, Long recordId) {
        return genericAttachmentRepository.findAllByFeatureNameAndRecordIdAndIsActiveTrue(featureName, recordId);
    }

    public void saveAllAttachments(Set<String> attachmentsLink, FeatureName featureName, Long recordId) {
        List<GenericAttachment> listOfAttachments = attachmentsLink.stream().map(link -> convertToEntity(featureName, recordId, link)).collect(Collectors.toList());
        genericAttachmentRepository.saveAll(listOfAttachments);
    }

    public void updateByRecordId(FeatureName featureName, Long recordId, Set<String> newLinks) {
        Set<GenericAttachment> listOfAttachments = genericAttachmentRepository.findAllByFeatureNameAndRecordIdAndIsActiveTrue(featureName, recordId);
        Set<String> oldLinks = listOfAttachments.stream().map(GenericAttachment::getLink).collect(Collectors.toSet());

        if (CollectionUtils.isEmpty(listOfAttachments) && CollectionUtils.isEmpty(newLinks) || newLinks.equals(oldLinks)) {
            return;
        }

        Set<GenericAttachment> deletableLinks = listOfAttachments.stream().filter(genericAttachment -> !newLinks.contains(genericAttachment.getLink()))
                .collect(Collectors.toSet()).stream().peek(genericAttachment -> genericAttachment.setIsActive(false)).collect(Collectors.toSet());

        Set<GenericAttachment> updatableLinks = newLinks.stream().filter(link -> !oldLinks.contains(link)).collect(Collectors.toSet())
                .stream().map(filterLink -> convertToEntity(featureName, recordId, filterLink)).collect(Collectors.toSet());

        updatableLinks.addAll(deletableLinks);
        genericAttachmentRepository.saveAll(updatableLinks);
    }

    private GenericAttachment convertToEntity(FeatureName featureName, Long recordId, String link) {
        return GenericAttachment.builder().
                recordId(recordId).link(link).featureName(featureName).build();
    }
}
