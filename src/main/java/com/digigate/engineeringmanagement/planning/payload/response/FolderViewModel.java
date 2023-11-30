package com.digigate.engineeringmanagement.planning.payload.response;

import com.digigate.engineeringmanagement.planning.constant.FileUploadItemType;
import lombok.*;

/**
 * Folder Response View Model
 *
 * @author Junaid Khan Pathan
 */

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FolderViewModel {
    private Long folderId;
    private FileUploadItemType uploadItemType;
    private String folderName;

    private String matchString;
    private String folderPath;

    private Boolean isActive;
}
