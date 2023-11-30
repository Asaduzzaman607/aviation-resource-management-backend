package com.digigate.engineeringmanagement.planning.payload.response;

import com.digigate.engineeringmanagement.planning.constant.ModelType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class AcBuildInHourInfo {

    private ModelType modelType;
    private Double acInHour;
    private Integer acInCycle;
    private Double tsn;
    private Integer csn;
    private String position;
    private LocalDate outDate;
}
