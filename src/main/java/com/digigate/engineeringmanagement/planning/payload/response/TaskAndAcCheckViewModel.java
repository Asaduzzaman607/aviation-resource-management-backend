package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * TaskAndAcCheck ViewModel
 *
 * @author Ashraful
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TaskAndAcCheckViewModel {
    List<TaskViewModelForAcCheck> taskViewModelForAcCheckList;
}
