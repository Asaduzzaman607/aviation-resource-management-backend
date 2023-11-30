package com.digigate.engineeringmanagement.planning.payload.request;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * AdReport SearchDto
 *
 * @author Ashraful
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdReportSearchDto {
    @NotNull
    private Long aircraftId;

    private LocalDate fromDate;

    private LocalDate toDate;

    private Integer intervalDay;
    private Double intervalHour;
    private Integer intervalCycle;
    private Integer thDay;
    private Double thHour;
    private Integer thCycle;
    private String taskSource;

    private String taskNo;

    private Boolean isPageable = false;
}
