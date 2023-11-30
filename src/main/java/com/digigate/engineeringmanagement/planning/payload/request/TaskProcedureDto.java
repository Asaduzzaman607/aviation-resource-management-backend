package com.digigate.engineeringmanagement.planning.payload.request;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskProcedureDto {
    private Long taskProcedureId;
    private Long positionId;
    private String jobProcedure;
}
