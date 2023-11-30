package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskConsumablePartViewModel {
    private Long taskConsumablePartId;
    private Long consumablePartId;
    private String partNo;
    private Long quantity;
}
