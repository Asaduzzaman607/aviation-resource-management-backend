package com.digigate.engineeringmanagement.planning.payload.response;

import com.digigate.engineeringmanagement.planning.constant.ModelType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Engine Time View Model
 *
 * @author Pranoy Das
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EngineTimeViewModel {
    private Long engineTimeId;

    private String nameExtension;

    private ModelType modelType;

    private LocalDate date;

    private Double hour;

    private Integer cycle;
}
