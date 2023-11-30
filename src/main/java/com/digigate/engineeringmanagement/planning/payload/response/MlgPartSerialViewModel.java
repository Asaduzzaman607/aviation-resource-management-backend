package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MlgPartSerialViewModel {
    private String partDescription;
    private Long partId;
    private Long serialId;
}
