package com.digigate.engineeringmanagement.planning.payload.response;

import com.digigate.engineeringmanagement.planning.constant.ModelType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Engine Shop Visit View Model
 *
 * @author Pranoy Das
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class EngineShopVisitViewModel {
    private Long engineShopVisitId;

    private ModelType modelType;

    private LocalDate date;

    private Double tsn;

    private Integer csn;

    private Double tso;

    private Integer cso;

    private String status;
}
