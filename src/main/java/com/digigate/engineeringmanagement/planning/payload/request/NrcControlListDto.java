package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.common.payload.IDto;
import lombok.*;

import java.time.LocalDate;

/**
 * Nrc ControlList Dto
 *
 * @author ashinisingha
 */

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class NrcControlListDto implements IDto {
    private Long aircraftCheckIndexId;
    private Long aircraftId;
    private Long woId;
    private LocalDate date;
}
