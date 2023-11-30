package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.planning.constant.FileUploadItemType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * File Search DTO
 *
 * @author Nafiul Islam
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileSearchDto {
    private FileUploadItemType uploadItemType;
    private String keywords;
}
