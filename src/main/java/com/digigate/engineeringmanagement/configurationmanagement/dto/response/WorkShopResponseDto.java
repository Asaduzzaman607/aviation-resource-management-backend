package com.digigate.engineeringmanagement.configurationmanagement.dto.response;

import com.digigate.engineeringmanagement.common.payload.IDto;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkShopResponseDto implements IDto {
    private Long id;
    private String code;
    private String address;
    private IdNameResponse city;
}
