package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkScopeTaskViewModel {
    private Long ldndId;
    private String taskNo;
    private String taskType;
    private Boolean isApuControl;
    private Integer intervalDay;
    private Double intervalHour;
    private Integer intervalCycle;
    private Integer thresholdDay;
    private Double thresholdHour;
    private Integer thresholdCycle;
    private TaskDescriptionViewModel taskDescriptionViewModel;
}
