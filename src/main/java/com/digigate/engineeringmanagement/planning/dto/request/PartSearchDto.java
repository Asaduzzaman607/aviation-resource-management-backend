package com.digigate.engineeringmanagement.planning.dto.request;

import com.digigate.engineeringmanagement.planning.constant.PartClassification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Part Search dto
 *
 * @author ashinisingha
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PartSearchDto {
    private Long modelId;
    private Long aircraftId;
    private Long acType;
    private Long partId;
    private Long serialId;
    private Long partSerialId;
    private String partNo;
    private String voucherNo; //dashboard search
    private PartClassification partClassification;
    private Boolean isActive;
    private Boolean isAvailPart = Boolean.FALSE;
}
