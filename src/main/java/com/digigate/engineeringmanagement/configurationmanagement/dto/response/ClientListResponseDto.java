package com.digigate.engineeringmanagement.configurationmanagement.dto.response;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClientListResponseDto {
    private Long id;
    private String clientName;
}
