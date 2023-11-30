package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

/**
 * Engine Model view Payload
 *
 * @author Pranoy Das
 */
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EngineModelViewPayload {
    private Long engineModelId;
    private Long aircraftId;
    private String aircraftName;
    private Integer engineModelTypeId;
    private String engineModelTypeName;
    private Long tsn;
    private Long csn;
    private String etRating;
    private String serialNo;
    private String position;
    private Long tsr;
    private Long csr;
    private  Long tso;
    private Long cso;
    private Boolean isActive;
}
