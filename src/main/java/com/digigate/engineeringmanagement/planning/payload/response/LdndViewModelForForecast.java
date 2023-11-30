package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LdndViewModelForForecast {
    private Long ldndId;
    private Long partId;
    private Long taskId;
    private String taskNo;
}
