package com.digigate.engineeringmanagement.planning.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AmlOilRecord dto
 *
 * @author Sayem Hasnat
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OilRecordSearchDto {
    private Long amlId;
    private Boolean isActive;
}

