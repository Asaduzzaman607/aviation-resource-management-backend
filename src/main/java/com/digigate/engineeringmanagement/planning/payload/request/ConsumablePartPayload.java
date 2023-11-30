package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.planning.entity.Part;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConsumablePartPayload {
    private Part part;
    private Long quantity;
}
