package com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StorePartAvailablityViewModelLite {
    private Long id;
    private String otherLocation;
}
