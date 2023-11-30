package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.common.payload.IDto;
import lombok.*;

import javax.validation.constraints.NotNull;

/**
 * Check Dto
 *
 * @author Ashraful
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CheckDto implements IDto {
    @NotNull
    private String title;
    private String description;
}
