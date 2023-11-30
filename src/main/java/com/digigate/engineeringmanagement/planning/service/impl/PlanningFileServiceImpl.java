package com.digigate.engineeringmanagement.planning.service.impl;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.common.service.AbstractService;
import com.digigate.engineeringmanagement.planning.entity.Folder;
import com.digigate.engineeringmanagement.planning.entity.PlanningFile;
import com.digigate.engineeringmanagement.planning.payload.request.FileSearchDto;
import com.digigate.engineeringmanagement.planning.payload.request.FolderDto;
import com.digigate.engineeringmanagement.planning.payload.request.PlanningFileDto;
import com.digigate.engineeringmanagement.planning.payload.request.ValidateMatchStringDto;
import com.digigate.engineeringmanagement.planning.payload.response.PlanningFileViewModel;
import com.digigate.engineeringmanagement.planning.repository.PlanningFileRepository;
import com.digigate.engineeringmanagement.planning.service.FolderService;
import com.digigate.engineeringmanagement.planning.service.PlanningFileService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * PlanningFile Service Implementation
 *
 * @author Junaid Khan Pathan
 */

@Service
public class PlanningFileServiceImpl
        extends AbstractService<PlanningFile, PlanningFileDto>
        implements PlanningFileService {
    private final PlanningFileRepository planningFileRepository;
    private final FolderService folderService;

    /**
     * Parameterized Constructor
     *
     * @param abstractRepository     {@link AbstractRepository}
     * @param planningFileRepository {@link PlanningFileRepository}
     * @param folderService          {@link FolderService}
     */
    public PlanningFileServiceImpl(AbstractRepository<PlanningFile> abstractRepository,
                                   PlanningFileRepository planningFileRepository,
                                   FolderService folderService) {
        super(abstractRepository);
        this.planningFileRepository = planningFileRepository;
        this.folderService = folderService;
    }

    /**
     * Responsible for getting a planning file by taking a planning file id
     *
     * @param id {@link Long}
     * @return returns a planning file
     */
    @Override
    public PlanningFile getPlanningFileById(Long id) {
        return super.findById(id);
    }

    /**
     * Responsible for getting all planning files inside a folder.
     *
     * @param folderId {@link Long}
     * @return returns a list of planning file view model inside a folder
     */
    @Override
    public List<PlanningFileViewModel> getAllPlanningFilesByFolderId(Long folderId) {
        Folder fileFolder = folderService.getFolderById(folderId);
        List<PlanningFile> foundPlanningFiles = planningFileRepository.findPlanningFilesByFolderOrderByFileName(fileFolder);
        return foundPlanningFiles.stream().map(this::convertToResponseDto).collect(Collectors.toList());
    }


    /**
     * Responsible for renaming an existing planning file name with a new name.
     *
     * @param planningFileDto {@link PlanningFileDto}
     * @param id              {@link Long}
     * @return returns a planning file view model with the updated name
     */
//    @Override
//    public PlanningFileViewModel renamePlanningFile(PlanningFileDto planningFileDto, Long id) {
//        PlanningFileViewModel planningFileViewModel = planningFileRepository.getPlanningFileByMatchString
//                (planningFileDto.getMatchString());
//        if(ObjectUtils.isNotEmpty(planningFileViewModel)){
//            throw EngineeringManagementServerException.badRequest(ErrorId.MATCH_STRING_ALREADY_EXISTS);
//        }
//        PlanningFile updatedPlanningFile = update(planningFileDto, id);
//        return convertToResponseDto(updatedPlanningFile);
//    }

    @Override
    public List<PlanningFileViewModel> getPlanningFilesBySearchKeyword(FileSearchDto fileSearchDto) {
        return planningFileRepository.getPlanningFilesBySearchKeyword(fileSearchDto.getUploadItemType(),
                fileSearchDto.getKeywords());
    }

//    @Override
//    public PlanningFileViewModel getPlanningFileByMatchString(String matchString) {
//        return planningFileRepository.getPlanningFileByMatchString(matchString);
//    }

    @Override
    public void uploadFile(List<PlanningFileDto> planningFileDto) {

        if(ObjectUtils.isEmpty(planningFileDto)){
            throw  EngineeringManagementServerException.badRequest(ErrorId.DATA_NOT_FOUND);
        }
        Optional<Folder> fileFolder = folderService.getOptionalFolderById(planningFileDto.get(0).getFolderId());
        if (fileFolder.isEmpty()) {
            throw EngineeringManagementServerException.badRequest(ErrorId.FOLDER_DOES_NOT_EXISTS);
        }

        List<PlanningFile> planningFileList = new ArrayList<>();
        for (PlanningFileDto fileDto : planningFileDto) {
            validateClientData(fileDto, fileDto.getId());
        }

        for(PlanningFileDto fileDto: planningFileDto){
          PlanningFile planningFile =  mapToEntity(fileDto,new PlanningFile());
          planningFileList.add(planningFile);
        }
        planningFileRepository.saveAll(planningFileList);
    }

//    @Override
//    public void validateMatchString(List<ValidateMatchStringDto> validateMatchStringDto) {
//        Set<String> matchStringFinder = new HashSet<>();
//        for (ValidateMatchStringDto matchStringDto : validateMatchStringDto) {
//            if (!StringUtils.isEmpty(matchStringDto.getMatchString()) &&
//                    !matchStringFinder.add(matchStringDto.getMatchString())) {
//                throw EngineeringManagementServerException.badRequest(ErrorId.DUPLICATE_MATCH_STRING);
//            }
//        }
//        List<PlanningFileViewModel> planningFileViewModels = planningFileRepository.getPlanningFileList(matchStringFinder);
//        if(ObjectUtils.isNotEmpty(planningFileViewModels)){
//            throw EngineeringManagementServerException.badRequest(ErrorId.MATCH_STRING_ALREADY_EXISTS);
//        }
//    }

    /**
     * Responsible for converting a planning file entity to a planning file view model response.
     *
     * @param planningFile {@link PlanningFile}
     * @return returns a planning file view model
     */
    @Override
    protected PlanningFileViewModel convertToResponseDto(PlanningFile planningFile) {
        return PlanningFileViewModel.builder()
                .fileId(planningFile.getId())
                .fileName(planningFile.getFileName())
                .fileUrl(planningFile.getFileUrl())
                .fileKey(planningFile.getFileKey())
                .folderId(planningFile.getFolder().getId())
                .folderName(planningFile.getFolder().getFolderName())
//                .matchString(planningFile.getMatchString())
                .isActive(planningFile.getIsActive())
                .build();
    }

    /**
     * Responsible for creating a new planning file entity from the values of a planning file dto.
     *
     * @param planningFileDto {@link PlanningFileDto}
     * @return returns a new planning file entity
     */
    @Override
    protected PlanningFile convertToEntity(PlanningFileDto planningFileDto) {
        return mapToEntity(planningFileDto, new PlanningFile());
    }

    /**
     * Responsible for populating planning file entity from the values of planning file dto on planning file update.
     *
     * @param dto    {@link PlanningFileDto}
     * @param entity {@link PlanningFile}
     * @return returns updated planning file entity
     */
    @Override
    protected PlanningFile updateEntity(PlanningFileDto dto, PlanningFile entity) {
        if (Objects.isNull(dto.getId())) {
            return mapToEntity(dto, entity);
        } else {
            entity.setFileName(dto.getFileName());
            return entity;
        }
    }

    /**
     * Responsible for populating planning file entity from the values of planning file dto.
     *
     * @param dto    {@link PlanningFileDto}
     * @param entity {@link PlanningFile}
     * @return returns a planning file entity
     */
    private PlanningFile mapToEntity(PlanningFileDto dto, PlanningFile entity) {
        if (Objects.nonNull(dto.getFolderId())) {
            Folder fileFolder = folderService.getFolderById(dto.getFolderId());
            entity.setFolder(fileFolder);
        }

        if (Objects.nonNull(dto.getFileName()) && !dto.getFileName().isEmpty()) {
            entity.setFileName(dto.getFileName());
        }

        if (Objects.nonNull(dto.getFileUrl()) && !dto.getFileUrl().isEmpty()) {
            entity.setFileUrl(dto.getFileUrl());
        }

        if (Objects.nonNull(dto.getFileKey()) && !dto.getFileKey().isEmpty()) {
            entity.setFileKey(dto.getFileKey());
        }
//        entity.setMatchString(dto.getMatchString());
        return entity;
    }

    /**
     * Responsible for validating planning file dto.
     *
     * @param planningFileDto {@link PlanningFileDto}
     * @param id              {@link Long}
     * @return returns true if dto is valid otherwise throws exception
     */
    @Override
    public Boolean validateClientData(PlanningFileDto planningFileDto, Long id) {
        if (Objects.isNull(id)) {
            if (Objects.isNull(planningFileDto.getFolderId())) {
                throw EngineeringManagementServerException.badRequest(ErrorId.FOLDER_NOT_INCLUDED);
            }

            if (Objects.isNull(planningFileDto.getFileUrl()) || planningFileDto.getFileUrl().length() < 1) {
                throw EngineeringManagementServerException.badRequest(ErrorId.INVALID_PLANNING_FILE_URL);
            }

            if (Objects.isNull(planningFileDto.getFileKey()) || planningFileDto.getFileKey().length() < 1) {
                throw EngineeringManagementServerException.badRequest(ErrorId.INVALID_PLANNING_FILE_KEY);
            }
        }

        if (planningFileDto.getFileName().length() < 1 || planningFileDto.getFileName().length() > 128) {
            throw EngineeringManagementServerException.badRequest(ErrorId.INVALID_PLANNING_FILE_NAME_LENGTH);
        }

        return true;
    }

    /**
     * Responsible for changing the active status of a planning file.
     *
     * @param id       {@link Long}
     * @param isActive {@link Boolean}
     */
    @Override
    public void updateActiveStatus(Long id, Boolean isActive) {
        PlanningFile planningFile = findById(id);
        if (planningFile.getIsActive().equals(isActive)) {
            throw EngineeringManagementServerException.badRequest(ErrorId.ONLY_TOGGLE_VALUE_ACCEPTED);
        }

        planningFile.setIsActive(isActive);
        saveItem(planningFile);
    }
}
