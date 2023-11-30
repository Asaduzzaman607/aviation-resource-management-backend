package com.digigate.engineeringmanagement.planning.repository;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.planning.constant.FileUploadItemType;
import com.digigate.engineeringmanagement.planning.entity.Folder;
import com.digigate.engineeringmanagement.planning.entity.PlanningFile;
import com.digigate.engineeringmanagement.planning.payload.response.PlanningFileViewModel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * PlanningFile repository
 *
 * @author Junaid Khan Pathan
 */

@Repository
public interface PlanningFileRepository extends AbstractRepository<PlanningFile> {
    @Query("select p from PlanningFile p where p.isActive=true and p.folder=:folder")
    List<PlanningFile> findPlanningFilesByFolderOrderByFileName(Folder folder);
    Optional<PlanningFile> findByFileNameAndIsActiveIsTrue(String name);

    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.PlanningFileViewModel(" +
            "p.id, " +
            "p.fileName, " +
            "p.fileUrl, " +
            "p.fileKey, " +
            "p.folderId, " +
            "p.folder.folderName, " +
//            "p.matchString, " +
            "p.isActive )" +
            "from PlanningFile p where p.isActive=true and p.folder.uploadItemType= :uploadItemType and " +
            "p.fileName like %:keywords%")
    List<PlanningFileViewModel> getPlanningFilesBySearchKeyword(FileUploadItemType uploadItemType, String keywords);

//    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.PlanningFileViewModel(" +
//            "p.id, " +
//            "p.fileName, " +
//            "p.fileUrl, " +
//            "p.fileKey, " +
//            "p.folderId, " +
//            "p.folder.folderName, " +
//            "p.matchString, " +
//            "p.isActive )" +
//            "from PlanningFile p where p.isActive=true and p.matchString= :matchString")
//    PlanningFileViewModel getPlanningFileByMatchString(String matchString);
//
//    @Query("select new com.digigate.engineeringmanagement.planning.payload.response.PlanningFileViewModel(" +
//            "p.id, " +
//            "p.fileName, " +
//            "p.fileUrl, " +
//            "p.fileKey, " +
//            "p.folderId, " +
//            "p.folder.folderName, " +
//            "p.matchString, " +
//            "p.isActive )" +
//            "from PlanningFile p where p.isActive=true and p.matchString in :matchString")
//    List<PlanningFileViewModel> getPlanningFileList(Set<String> matchString);
}
