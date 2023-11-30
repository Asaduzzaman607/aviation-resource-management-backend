package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

/**
 * LdndForTask ViewModel
 *
 * @author Ashraful
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LdndForTaskViewModel {

    private String taskNo;

    private Long ldndId;

    private String taskDescription;

    private String partNo;

    private String serialNo;

    private Double intervalHour;
    private Integer intervalCycle;
    private Integer intervalDay;
    private Double thresholdHour;
    private Integer thresholdCycle;
    private Integer thresholdDay;
    private Boolean isApuControl;

    public LdndForTaskViewModel(String taskNo, Long ldndId, String taskDescription, String partNo, String serialNo) {
        this.taskNo = taskNo;
        this.ldndId = ldndId;
        this.taskDescription = taskDescription;
        this.partNo = partNo;
        this.serialNo = serialNo;
    }

    public LdndForTaskViewModel(String taskNo, Long ldndId) {
        this.taskNo = taskNo;
        this.ldndId = ldndId;
    }
}
