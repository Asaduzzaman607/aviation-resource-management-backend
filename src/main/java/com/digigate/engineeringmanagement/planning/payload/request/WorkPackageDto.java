package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.common.payload.IDto;
import com.digigate.engineeringmanagement.planning.constant.PackageType;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

/**
 * Work Package Dto
 *
 * @author ashinisingha
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class WorkPackageDto implements IDto {
    @NotNull
    private Long acCheckIndexId;
    @NotNull
    private Long aircraftId;
    private PackageType packageType;
    private LocalDate inputDate;
    private LocalDate releaseDate;
    @NotNull
    private Double acHours;
    @NotNull
    private Integer acCycle;

    private List<JobCardsDto> jobCardsDtoList;
    private LocalDate asOfDate;
}
