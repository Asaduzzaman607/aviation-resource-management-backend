package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class CabinViewModel {
    private Long cabinId;
    private String codeTitle;
    private Boolean activeStatus;
}
