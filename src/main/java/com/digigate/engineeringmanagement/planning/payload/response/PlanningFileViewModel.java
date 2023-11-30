package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

/**
 * PlanningFile Response View Model
 *
 * @author Junaid Khan Pathan
 */

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanningFileViewModel {
    private Long fileId;
    private String fileName;
    private String fileUrl;
    private String fileKey;
    private Long folderId;
    private String folderName;
//    private String matchString;
    private Boolean isActive;
}
