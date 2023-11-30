package com.digigate.engineeringmanagement.planning.payload.response;

import com.digigate.engineeringmanagement.planning.entity.TaskConsumablePart;
import lombok.*;

import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConsumablePartTaskViewModel {
    private Long taskId;
    private Set<TaskConsumablePart> taskConsumablePartSet;
}
