package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.common.payload.SDto;
import lombok.*;


import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Work Order Search Dto
 *
 * @author ashinisingha
 */
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkOrderSearchDto implements SDto {
    @NotNull
    private Long aircraftId;
    private LocalDate date;
    private Boolean isActive;
}
