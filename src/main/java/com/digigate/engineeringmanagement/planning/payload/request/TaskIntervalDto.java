package com.digigate.engineeringmanagement.planning.payload.request;

import lombok.*;

import javax.validation.constraints.NotNull;

/**
 * Task Interval Dto
 *
 * @author Pranoy Das
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskIntervalDto {
    private Long taskIntervalId;
    @NotNull
    private Integer intervalType;
    @NotNull
    private Double intervalValue;
    @NotNull
    private Integer intervalUnit;
}
