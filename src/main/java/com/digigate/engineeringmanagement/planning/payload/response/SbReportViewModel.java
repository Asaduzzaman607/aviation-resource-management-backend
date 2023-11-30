package com.digigate.engineeringmanagement.planning.payload.response;

import com.digigate.engineeringmanagement.planning.constant.TaskStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SbReportViewModel {
    private String sbNo;
    private String taskDescription;
    private String relatedAd;
    private TaskStatusEnum status;
    private Double doneHour;
    private Integer doneCycle;
    private LocalDate doneDate;
    private String remark;
    private String ata;
}
