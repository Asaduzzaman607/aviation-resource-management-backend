package com.digigate.engineeringmanagement.logistic.payload.request;

import com.digigate.engineeringmanagement.common.payload.IDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PoTrackerLocationRequestDto implements IDto {
    private Long id;
    private String location;
    private LocalDate date;
    private String awbNo;
}
