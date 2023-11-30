package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.common.payload.IDto;
import com.digigate.engineeringmanagement.planning.constant.LifeLimitUnit;
import com.digigate.engineeringmanagement.planning.constant.PartClassification;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


/**
 * Part Dto
 *
 * @author ashinisingha
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class PartDto implements IDto {
    private Long id;
    private Long modelId;
    @NotBlank
    private String partNo;
    private String description;
    private Double countFactor;
    @NotNull
    private PartClassification classification;
    private Long unitOfMeasureId;
    Set<Long> alternatePartIds;
    List<Long> partWiseUomIds;
    private Boolean isActive;
    private Long lifeLimit;
    private LifeLimitUnit lifeLimitUnit;
}
