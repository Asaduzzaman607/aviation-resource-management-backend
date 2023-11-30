package com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand;

import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StorePartSerialViewModelLite {
    private LocalDate validTill;
    private LocalDate selfLife;
    private String grnNo;
}
