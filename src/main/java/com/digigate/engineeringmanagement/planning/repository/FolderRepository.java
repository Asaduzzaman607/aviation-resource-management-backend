package com.digigate.engineeringmanagement.planning.repository;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.planning.constant.FileUploadItemType;
import com.digigate.engineeringmanagement.planning.entity.Folder;
import com.digigate.engineeringmanagement.planning.payload.request.FolderDto;
import com.digigate.engineeringmanagement.planning.payload.response.FolderViewModel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Folder repository
 *
 * @author Junaid Khan Pathan
 */

@Repository
public interface FolderRepository extends AbstractRepository<Folder> {
    Optional<Folder> findByFolderNameAndIsActiveTrue(String name);
    @Query("select f from Folder f where f.isActive=true and f.uploadItemType=:uploadItemType")
    List<Folder> findAllByUploadItemTypeOrderByFolderName(FileUploadItemType uploadItemType);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.request.FolderDto(" +
            "f.id, " +
            "f.uploadItemType, " +
            "f.folderName, " +
            "f.matchString, " +
            "f.folderPath " +
            ")" +
            "from Folder f where f.isActive=true and f.matchString= :matchString")
    FolderDto getPlanningFileByMatchString(String matchString);
}
