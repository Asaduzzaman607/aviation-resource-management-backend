package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ManHourTaskViewModel {
    private Long ldndId;
    private String jobProcedure;
    private String taskNo;
    private TaskDescriptionViewModel taskDescriptionViewModel;
    private String taskType;
    private String trade;
    private String manHours;
    private String proposedManHours;
    private Integer noOfMan;
    private Double elapsedTime;
    private Double actualManHour;
}
