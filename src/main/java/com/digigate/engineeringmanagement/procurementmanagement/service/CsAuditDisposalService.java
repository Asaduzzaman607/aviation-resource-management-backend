package com.digigate.engineeringmanagement.procurementmanagement.service;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.entity.User;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.service.AbstractService;
import com.digigate.engineeringmanagement.common.service.UserService;
import com.digigate.engineeringmanagement.common.util.Helper;
import com.digigate.engineeringmanagement.procurementmanagement.dto.request.CsAuditDisposalDto;
import com.digigate.engineeringmanagement.procurementmanagement.dto.response.CsAuditDisposalResponseDto;
import com.digigate.engineeringmanagement.procurementmanagement.entity.CsAuditDisposal;
import com.digigate.engineeringmanagement.procurementmanagement.repository.CsAuditDisposalRepository;
import com.digigate.engineeringmanagement.storemanagement.constant.FeatureName;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.GenericAttachment;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.UsernameProjection;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.GenericAttachmentService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CsAuditDisposalService extends AbstractService<CsAuditDisposal, CsAuditDisposalDto> {

    private final CsAuditDisposalRepository csAuditDisposalRepository;
    private final UserService userService;

    private final GenericAttachmentService genericAttachmentService;
    public CsAuditDisposalService(CsAuditDisposalRepository repository, UserService userService, GenericAttachmentService genericAttachmentService) {
        super(repository);
        this.csAuditDisposalRepository = repository;
        this.userService = userService;
        this.genericAttachmentService = genericAttachmentService;
    }

    @Override
    public CsAuditDisposal create(CsAuditDisposalDto csAuditDisposalDto) {
        validateClientData(csAuditDisposalDto, null);
        CsAuditDisposal entity = convertToEntity(csAuditDisposalDto);
        CsAuditDisposal csAuditDisposal = saveItem(entity);
        if (!CollectionUtils.isEmpty(csAuditDisposalDto.getAttachments())) {
            genericAttachmentService.saveAllAttachments(csAuditDisposalDto.getAttachments(), FeatureName.CS_AUDIT_DISPOSAL, csAuditDisposal.getId());
        }
        return csAuditDisposal;
    }

    @Override
    public CsAuditDisposal update(CsAuditDisposalDto csAuditDisposalDto, Long id) {
        validateClientData(csAuditDisposalDto, id);
        final CsAuditDisposal entity = updateEntity(csAuditDisposalDto, findByIdUnfiltered(id));
        genericAttachmentService.updateByRecordId(FeatureName.CS_AUDIT_DISPOSAL, entity.getId(), csAuditDisposalDto.getAttachments());
        return saveItem(entity);
    }


    @Override
    protected CsAuditDisposalResponseDto convertToResponseDto(CsAuditDisposal csAuditDisposal) {
        return populateResponseDto(Collections.singletonList(csAuditDisposal)).get(ApplicationConstant.FIRST_INDEX);
    }

    @Override
    protected CsAuditDisposal convertToEntity(CsAuditDisposalDto csAuditDisposalDto) {
        CsAuditDisposal csAuditDisposal = new CsAuditDisposal();
        csAuditDisposal.setAuditDisposal(csAuditDisposalDto.getAuditDisposal());
        csAuditDisposal.setCsPartDetail(csAuditDisposal.getCsPartDetailWithId(csAuditDisposalDto.getCsPartDetailId()));
        csAuditDisposal.setSubmittedBy(User.withId(Helper.getAuthUserId()));
        return csAuditDisposal;
    }

    public List<CsAuditDisposalResponseDto> findByItemPartId(Long id){
        return populateResponseDto(csAuditDisposalRepository.findByCsPartDetailId(id));
    }

    public List<CsAuditDisposalResponseDto> populateResponseDto(List<CsAuditDisposal> csAuditDisposalList){

        Set<Long> submittedByIdSet = csAuditDisposalList.stream().map(CsAuditDisposal::getSubmittedById).collect(Collectors.toSet());
        Set<Long> csAuditDisposalIds = csAuditDisposalList.stream().map(CsAuditDisposal::getId).collect(Collectors.toSet());
        Map<Long, Set<String>> attachmentLinksMap = genericAttachmentService.getAllAttachmentByFeatureNameAndId(FeatureName.CS_AUDIT_DISPOSAL, csAuditDisposalIds)
                .stream().collect(Collectors.groupingBy(GenericAttachment::getRecordId, Collectors.mapping(GenericAttachment::getLink, Collectors.toSet())));
        Map<Long, UsernameProjection> usernameProjectionMap = userService.findUsernameByIdList(submittedByIdSet).stream()
                .collect(Collectors.toMap(UsernameProjection::getId, Function.identity()));

        return csAuditDisposalList.stream().map(csAuditDisposal -> convertToViewModel(csAuditDisposal, usernameProjectionMap, attachmentLinksMap)).collect(Collectors.toList());
    }

    private CsAuditDisposalResponseDto convertToViewModel(CsAuditDisposal csAuditDisposal, Map<Long, UsernameProjection> usernameProjectionMap, Map<Long, Set<String>> attachmentLinksMap){

        return CsAuditDisposalResponseDto.builder()
                .id(csAuditDisposal.getId())
                .auditDisposal(csAuditDisposal.getAuditDisposal())
                .attachments(CollectionUtils.isNotEmpty(attachmentLinksMap.get(csAuditDisposal.getId())) ?
                        attachmentLinksMap.get(csAuditDisposal.getId()) : new HashSet<>())
                .submittedById(Objects.nonNull(csAuditDisposal.getSubmittedById()) ?
                        usernameProjectionMap.get(csAuditDisposal.getSubmittedById()).getId() : null)
                .submittedByName(Objects.nonNull(csAuditDisposal.getSubmittedById()) ?
                        usernameProjectionMap.get(csAuditDisposal.getSubmittedById()).getLogin() : null)
                .build();
    }

    @Override
    protected CsAuditDisposal updateEntity(CsAuditDisposalDto dto, CsAuditDisposal entity) {
        if(!Objects.equals(Helper.getAuthUserId(), entity.getSubmittedById())){
            throw EngineeringManagementServerException.badRequest(ErrorId.ACCESS_DENIED);
        }
        entity.setAuditDisposal(dto.getAuditDisposal());
        entity.setCsPartDetail(entity.getCsPartDetailWithId(dto.getCsPartDetailId()));
        return entity;
    }
}
