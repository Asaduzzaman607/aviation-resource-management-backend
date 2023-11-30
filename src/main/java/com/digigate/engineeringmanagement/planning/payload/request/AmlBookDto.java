package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.common.payload.IDto;
import lombok.*;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;

/**
 * AML Book Dto
 *
 * @author ashinisingha
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class AmlBookDto implements IDto {
    @NotNull
    private Long aircraftId;
    private String bookNo;
    @NotNull
    private Integer startPageNo;
    @NotNull
    private Integer endPageNo;
    private Boolean isActive;
}
