package com.digigate.engineeringmanagement.planning.service;

import com.digigate.engineeringmanagement.planning.constant.FileUploadItemType;
import com.digigate.engineeringmanagement.planning.entity.Folder;
import com.digigate.engineeringmanagement.planning.payload.request.FolderDto;
import com.digigate.engineeringmanagement.planning.payload.response.FolderViewModel;

import java.util.List;
import java.util.Optional;

/**
 * Folder Service
 *
 * @author Junaid Khan Pathan
 */

public interface FolderService {
    FolderViewModel renameFolder(FolderDto folderDto, Long id);
    Folder getFolderById(Long id);
    Optional<Folder> getOptionalFolderById(Long id);
    List<FolderViewModel> getAllFolderByUploadItemType(FileUploadItemType uploadItemType);

    FolderDto getPlanningFileByMatchString(String matchString);
}
