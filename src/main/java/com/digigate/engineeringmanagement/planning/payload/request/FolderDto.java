package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.payload.IDto;
import com.digigate.engineeringmanagement.planning.constant.FileUploadItemType;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Folder Request Payload DTO
 *
 * @author Junaid Khan Pathan
 */

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FolderDto implements IDto {
    private Long id;
    private FileUploadItemType uploadItemType;

    @NotNull
    @Size(min = ApplicationConstant.MIN_FILE_FOLDER_SIZE, max = ApplicationConstant.MAX_FILE_FOLDER_SIZE)
    private String folderName;

    private String matchString;
    private String folderPath;
}
