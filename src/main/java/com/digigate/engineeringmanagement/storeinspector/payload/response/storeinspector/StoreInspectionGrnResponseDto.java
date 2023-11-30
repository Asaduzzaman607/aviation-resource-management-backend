package com.digigate.engineeringmanagement.storeinspector.payload.response.storeinspector;

import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StoreInspectionGrnResponseDto {
    private Long id;
    private String grnNo;
    private LocalDate createdDate;
    private boolean used;

}
