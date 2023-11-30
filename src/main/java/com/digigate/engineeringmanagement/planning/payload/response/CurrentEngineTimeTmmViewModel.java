package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CurrentEngineTimeTmmViewModel {
    private Double hour;
    private Integer cycle;
}
