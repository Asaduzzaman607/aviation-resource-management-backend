package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ForecastTaskViewModel {
    private Long forecastTaskId;
    private Long ldndId;
    private String taskNo;
    private LocalDate dueDate;
    private String comment;
    List<ForecastTaskPartViewModel> forecastTaskPartViewModels;
}
