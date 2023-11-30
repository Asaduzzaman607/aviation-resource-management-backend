package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.planning.constant.ModelType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Engine Time Dto
 *
 * @author Pranoy Das
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EngineTimeDto {
    private Long engineTimeId;

    @NotNull
    private ModelType modelType;

    private LocalDate date;

    private Double hour;

    private Integer cycle;
}
