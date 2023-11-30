package com.digigate.engineeringmanagement.planning.controller;

import com.digigate.engineeringmanagement.common.controller.AbstractController;
import com.digigate.engineeringmanagement.common.service.IService;
import com.digigate.engineeringmanagement.planning.constant.FileUploadItemType;
import com.digigate.engineeringmanagement.planning.entity.Folder;
import com.digigate.engineeringmanagement.planning.payload.request.FolderDto;
import com.digigate.engineeringmanagement.planning.payload.response.FolderViewModel;
import com.digigate.engineeringmanagement.planning.service.FolderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Folder Controller
 *
 * @author Junaid Khan Pathan
 */

@RestController
@RequestMapping("/api/folders")
public class FolderController extends AbstractController<Folder, FolderDto> {
    private final FolderService folderService;

    /**
     * Parameterized Constructor
     *
     * @param iService          {@link IService}
     * @param folderService     {@link FolderService}
     */
    public FolderController(IService<Folder, FolderDto> iService,
                            FolderService folderService) {
        super(iService);
        this.folderService = folderService;
    }

    /**
     * This endpoint takes id of an existing folder and new folder name embedded in request body dto
     * and sends the renamed folder.
     *
     * @param folderDto     {@link FolderDto}
     * @param id            {@link Long}
     * @return              returns a folder with the updated name
     */
    @PatchMapping("/{id}/rename")
    public ResponseEntity<FolderViewModel> renameFolder(@Valid @RequestBody FolderDto folderDto,
                                                        @PathVariable("id") Long id) {
        return new ResponseEntity<>(folderService.renameFolder(folderDto, id), HttpStatus.OK);
    }

    /**
     * This endpoint takes upload item type of folder and returns a list of all folders under this type.
     *
     * @param typeId        {@link FileUploadItemType}
     * @return              returns a list of all folders under this type
     */
    @GetMapping("/type/{typeId}")
    public ResponseEntity<List<FolderViewModel>> getAllFolderByUploadItemType(@PathVariable("typeId") Integer typeId) {
        return new ResponseEntity<>(folderService.getAllFolderByUploadItemType(FileUploadItemType.get(typeId)), HttpStatus.OK);
    }

    @GetMapping("/search_match_string/{matchString}")
    public ResponseEntity<FolderDto> getPlanningFileByMatchString(@PathVariable("matchString")
                                                                        String matchString) {
        return new ResponseEntity<>(folderService.getPlanningFileByMatchString(matchString), HttpStatus.OK);
    }
}
