package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.common.payload.SDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * CheckSearch Dto
 *
 * @author Ashraful
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CheckSearchDto implements SDto {
    private Boolean isActive;
    private String title;
}
