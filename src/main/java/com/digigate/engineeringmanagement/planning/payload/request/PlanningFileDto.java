package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.payload.IDto;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * PlanningFile Request Payload DTO
 *
 * @author Junaid Khan Pathan
 */

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanningFileDto implements IDto {
    private Long id;
    private Long folderId;

    @NotNull
    @Size(min = ApplicationConstant.MIN_FILE_FOLDER_SIZE, max = ApplicationConstant.MAX_FILE_FOLDER_SIZE)
    private String fileName;

    private String fileUrl;
    private String fileKey;
//    private String matchString;

}
