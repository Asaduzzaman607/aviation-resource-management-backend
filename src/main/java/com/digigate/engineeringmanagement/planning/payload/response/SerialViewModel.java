package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
 * Part repository
 *
 * @author Nafiul Islam
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SerialViewModel {
    private Long id;
    private Long partId;
    private String serialNumber;
    private String partNo;
    private Boolean isActive;
}
