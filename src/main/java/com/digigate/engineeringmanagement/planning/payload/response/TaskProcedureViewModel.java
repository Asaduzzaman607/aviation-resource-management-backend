package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskProcedureViewModel {
    private Long taskProcedureId;
    private Long positionId;
    private String name;
    private String jobProcedure;
}
