package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.common.payload.IDto;
import lombok.*;

/**
 * Engine Model request payload
 *
 * @author Pranoy Das
 */
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EngineModelDto implements IDto {
    private Long engineModelId;
    private Long aircraftId;
    private Integer engineModelTypeId;
    private Long tsn;
    private Long csn;
    private String etRating;
    private String serialNo;
    private String position;
    private Long tsr;
    private Long csr;
    private  Long tso;
    private Long cso;
}
