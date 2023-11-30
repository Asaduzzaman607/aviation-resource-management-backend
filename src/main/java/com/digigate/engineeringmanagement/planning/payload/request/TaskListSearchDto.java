package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.planning.constant.TaskSourceEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskListSearchDto {

    @NotNull
    private TaskSourceEnum type;

    @NotNull
    private String taskNo;
}
