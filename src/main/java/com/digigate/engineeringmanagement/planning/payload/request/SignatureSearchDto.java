package com.digigate.engineeringmanagement.planning.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Signature search dto
 *
 * @author ashinisingha
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignatureSearchDto {
    private String employeeName;
    private String authNo;
    private Boolean isActive;
}
