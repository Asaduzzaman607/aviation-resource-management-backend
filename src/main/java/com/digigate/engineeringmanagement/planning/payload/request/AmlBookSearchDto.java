package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.common.payload.SDto;
import lombok.*;

/**
 * AML Book search Dto
 *
 * @author ashinisingha
 */
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AmlBookSearchDto implements SDto {
    private Long aircraftId;
    private String bookNo;
    private Boolean isActive;
}
