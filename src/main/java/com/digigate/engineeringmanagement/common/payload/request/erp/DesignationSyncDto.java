
package com.digigate.engineeringmanagement.common.payload.request.erp;

import lombok.Data;

import java.util.List;

@Data
public class DesignationSyncDto {
    private Long companyId;
    private List<DesignationDataDto> designations;
}
