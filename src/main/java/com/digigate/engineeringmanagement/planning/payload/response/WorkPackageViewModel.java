package com.digigate.engineeringmanagement.planning.payload.response;

import com.digigate.engineeringmanagement.planning.constant.PackageType;
import com.digigate.engineeringmanagement.planning.payload.request.JobCardsDto;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Work Package View Model
 *
 * @author ashinisingha
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class WorkPackageViewModel {
    private Long workPackageId;
    private Long acCheckIndexId;
    private String checkName;
    private Long aircraftId;
    private String aircraftName;
    private PackageType packageType;
    private LocalDate inputDate;
    private LocalDate releaseDate;
    private Double acHours;
    private Integer acCycle;
    private Boolean isActive;

    private List<JobCardsViewModel> jobCardsViewModels;
    private LocalDate asOfDate;
}
