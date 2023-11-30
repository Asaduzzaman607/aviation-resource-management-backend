package com.digigate.engineeringmanagement.planning.payload.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class TaskComponentResponse {

    private Long taskId;
    private String taskNo;
    private String jobProcedure;
    private String taskType;
    private Double tboIntervalHour;
    private Integer tboIntervalCycle;
    private Integer tboIntervalDay;
    private Boolean tboIsApuControl;
    private Boolean isActive;

    @JsonIgnore
    public Long getTaskId() {
        return taskId;
    }

    @JsonIgnore
    public String getJobProcedure() {
        return jobProcedure;
    }

    @JsonIgnore
    public String getTaskType() {
        return taskType;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof TaskComponentResponse)) return false;
        return Objects.nonNull(this.getTaskNo()) && this.getTaskNo().equals(((TaskComponentResponse) object).getTaskNo());
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getTaskNo())) {
            return this.getClass().hashCode();
        }
        return this.getTaskNo().hashCode();
    }
}
