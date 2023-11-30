package com.digigate.engineeringmanagement.planning.service.impl;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.common.service.AbstractService;
import com.digigate.engineeringmanagement.planning.constant.FileUploadItemType;
import com.digigate.engineeringmanagement.planning.entity.Folder;
import com.digigate.engineeringmanagement.planning.entity.PlanningFile;
import com.digigate.engineeringmanagement.planning.payload.request.FolderDto;
import com.digigate.engineeringmanagement.planning.payload.response.FolderViewModel;
import com.digigate.engineeringmanagement.planning.repository.FolderRepository;
import com.digigate.engineeringmanagement.planning.service.FolderService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Folder Service Implementation
 *
 * @author Junaid Khan Pathan
 */

@Service
public class FolderServiceImpl
        extends AbstractService<Folder, FolderDto>
        implements FolderService {
    private final FolderRepository folderRepository;

    /**
     * Parameterized Constructor
     *
     * @param abstractRepository {@link AbstractRepository}
     * @param folderRepository   {@link FolderRepository}
     */
    public FolderServiceImpl(AbstractRepository<Folder> abstractRepository,
                             FolderRepository folderRepository) {
        super(abstractRepository);
        this.folderRepository = folderRepository;
    }

    /**
     * Responsible for getting a folder by taking a folder id
     *
     * @param id {@link Long}
     * @return returns a folder
     */
    @Override
    public Folder getFolderById(Long id) {
        return super.findById(id);
    }

    /**
     * Responsible for getting an optional of folder by taking a folder id
     *
     * @param id {@link Long}
     * @return returns an optional of folder
     */
    @Override
    public Optional<Folder> getOptionalFolderById(Long id) {
        return folderRepository.findById(id);
    }

    /**
     * Responsible for getting all folders under a specific upload item type.
     *
     * @param uploadItemType {@link FileUploadItemType}
     * @return returns a list of folder view model
     */
    @Override
    public List<FolderViewModel> getAllFolderByUploadItemType(FileUploadItemType uploadItemType) {
        List<Folder> foundFolders = folderRepository.findAllByUploadItemTypeOrderByFolderName(uploadItemType);
        return foundFolders.stream().map(this::convertToResponseDto).collect(Collectors.toList());
    }

    @Override
    public FolderDto getPlanningFileByMatchString(String matchString) {
        return folderRepository.getPlanningFileByMatchString(matchString);
    }

    /**
     * Responsible for converting a folder entity to a folder view model response.
     *
     * @param folder {@link Folder}
     * @return returns a folder view model
     */
    @Override
    protected FolderViewModel convertToResponseDto(Folder folder) {
        return FolderViewModel.builder()
                .folderId(folder.getId())
                .folderName(folder.getFolderName())
                .uploadItemType(folder.getUploadItemType())
                .matchString(folder.getMatchString())
                .folderPath(folder.getFolderPath())
                .isActive(folder.getIsActive())
                .build();
    }

    /**
     * Responsible for creating a new folder entity from the values of a folder dto.
     *
     * @param folderDto {@link FolderDto}
     * @return returns a new folder entity
     */
    @Override
    protected Folder convertToEntity(FolderDto folderDto) {
        return mapToEntity(folderDto, new Folder());
    }


    /**
     * Responsible for populating folder entity from the values of folder dto on folder update.
     *
     * @param dto    {@link FolderDto}
     * @param entity {@link Folder}
     * @return returns populated folder entity
     */
    @Override
    protected Folder updateEntity(FolderDto dto, Folder entity) {
        if (Objects.isNull(dto.getId())) {
            return mapToEntity(dto, entity);
        } else {
            entity.setFolderName(dto.getFolderName());
            entity.setMatchString(dto.getMatchString());
            return entity;
        }
    }

    /**
     * Responsible for populating folder entity from the values of folder dto.
     *
     * @param dto    {@link FolderDto}
     * @param entity {@link Folder}
     * @return returns a folder entity
     */
    private Folder mapToEntity(FolderDto dto, Folder entity) {
        if (Objects.nonNull(dto.getFolderName()) && !dto.getFolderName().isEmpty()) {
            entity.setFolderName(dto.getFolderName());
        }

        if (Objects.nonNull(dto.getUploadItemType())) {
            entity.setUploadItemType(dto.getUploadItemType());
        }

        if(Objects.nonNull(dto.getMatchString())){
            entity.setMatchString(dto.getMatchString());
        }

        if(Objects.nonNull(dto.getFolderPath())){
            entity.setFolderPath(dto.getFolderPath());
        }
        return entity;
    }

    /**
     * Responsible for validating folder dto.
     *
     * @param folderDto {@link FolderDto}
     * @param id        {@link Long}
     * @return returns true if dto is valid otherwise throws exception
     */
    @Override
    public Boolean validateClientData(FolderDto folderDto, Long id) {
        if (Objects.isNull(id)) {
            if (Objects.isNull(folderDto.getUploadItemType())) {
                throw EngineeringManagementServerException.badRequest(ErrorId.FOLDER_TYPE_REQUIRED);
            }
        }

        if (folderDto.getFolderName().length() < ApplicationConstant.MIN_FILE_FOLDER_SIZE
                || folderDto.getFolderName().length() > ApplicationConstant.MAX_FILE_FOLDER_SIZE) {
            throw EngineeringManagementServerException.badRequest(ErrorId.INVALID_FOLDER_NAME_LENGTH);
        }

        if(Objects.nonNull(folderDto.getMatchString())){
            FolderDto folderDtoItem = folderRepository.getPlanningFileByMatchString(folderDto.getMatchString());
            if(ObjectUtils.isNotEmpty(folderDtoItem) && !(folderDtoItem.getId().equals(id))){
                throw EngineeringManagementServerException.badRequest(ErrorId.MATCH_STRING_ALREADY_EXISTS);
            }
        }
        return true;
    }

    /**
     * Responsible for renaming an existing folder name with a new name.
     *
     * @param folderDto {@link FolderDto}
     * @param id        {@link Long}
     * @return returns a folder view model with the updated name
     */
    @Override
    public FolderViewModel renameFolder(FolderDto folderDto, Long id) {
        Folder updatedFolder = update(folderDto, id);
        return convertToResponseDto(updatedFolder);
    }

    /**
     * Responsible for changing the active status of a folder.
     *
     * @param id       {@link Long}
     * @param isActive {@link Boolean}
     */
    @Override
    public void updateActiveStatus(Long id, Boolean isActive) {
        Folder folder = findById(id);
        if (folder.getIsActive().equals(isActive)) {
            throw EngineeringManagementServerException.badRequest(ErrorId.ONLY_TOGGLE_VALUE_ACCEPTED);
        }

        if (Boolean.FALSE.equals(isActive)) {
            if (!folder.getPlanningFiles().isEmpty()) {
                Set<PlanningFile> planningFiles = folder.getPlanningFiles();
                planningFiles.stream()
                        .filter(file -> file.getIsActive().equals(Boolean.TRUE))
                        .findAny()
                        .ifPresent(e -> {
                            throw EngineeringManagementServerException.badRequest(ErrorId.FOLDER_IS_NOT_EMPTY);
                        });
            }
        }

        folder.setIsActive(isActive);
        saveItem(folder);
    }
}
