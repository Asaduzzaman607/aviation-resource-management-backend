package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Signature View model
 *
 * @author ashinisingha
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SignatureViewModel {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private String authNo;
    private Boolean isActive;
}
