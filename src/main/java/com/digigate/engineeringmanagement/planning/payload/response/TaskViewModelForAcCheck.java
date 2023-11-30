package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This view model is for Task Id and Task No
 *
 * @author Ashraful
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TaskViewModelForAcCheck {

    public Long taskId;

    public String taskNo;
}
