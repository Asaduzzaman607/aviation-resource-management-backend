package com.digigate.engineeringmanagement.planning.payload.request;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskConsumablePartDto {
    private Long taskConsumablePartId;
    private Long consumablePartId;
    private String partNo;
    private Long quantity;
}
