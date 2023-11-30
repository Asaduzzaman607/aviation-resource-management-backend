package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskDescriptionViewModel {
    private String taskDescription;
    private String partNo;
    private String serialNo;
    private Long serialId;
}
