package com.digigate.engineeringmanagement.planning.payload.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class ForecastRequest {
    @NotNull
    private Long ldndId;
    @NotNull
    private Long taskId;
    @NotNull
    private Long partId;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;
}
