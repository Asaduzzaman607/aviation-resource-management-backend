package com.digigate.engineeringmanagement.common.payload.response;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * Action view model
 *
 * @author Pranoy Das
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActionViewModel {
    private Integer actionId;
    private String actionName;
    private Integer accessRightId;
}
