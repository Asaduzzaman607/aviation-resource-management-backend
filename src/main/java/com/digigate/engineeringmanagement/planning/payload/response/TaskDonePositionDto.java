package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

/**
 * Dto for Task Done Position List
 *
 * @author  Asifur Rahman
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskDonePositionDto {
    private Long procedureId;
    private String position;
}
