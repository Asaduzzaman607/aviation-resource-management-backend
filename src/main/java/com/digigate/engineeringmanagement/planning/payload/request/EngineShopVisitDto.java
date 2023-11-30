package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.common.payload.IDto;
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
 * Engine Shop Visit Dto
 *
 * @author Pranoy Das
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EngineShopVisitDto implements IDto {
    private Long engineShopVisitId;

    @NotNull
    private ModelType modelType;

    private LocalDate date;

    private Double tsn;

    private Integer csn;

    private Double tso;

    private Integer cso;

    private String status;
}
