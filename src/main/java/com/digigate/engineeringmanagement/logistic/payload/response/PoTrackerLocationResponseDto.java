package com.digigate.engineeringmanagement.logistic.payload.response;

import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PoTrackerLocationResponseDto {
    private Long id;
    private String location;
    private Long trackerId;
    private LocalDate date;
    private String awbNo;
}
