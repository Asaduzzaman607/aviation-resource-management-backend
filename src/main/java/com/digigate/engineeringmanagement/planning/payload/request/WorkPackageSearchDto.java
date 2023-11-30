package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.common.payload.SDto;
import com.digigate.engineeringmanagement.planning.constant.PackageType;
import lombok.*;

import javax.validation.constraints.NotNull;

/**
 * Work Package Search Dto
 *
 * @author ashinisingha
 */
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkPackageSearchDto implements SDto {
    private Long aircraftId;
    private PackageType packageType;
    private Boolean isActive;
}
