package com.digigate.engineeringmanagement.configurationmanagement.dto.request.configuration;

import com.digigate.engineeringmanagement.common.payload.IDto;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QualitySaveValidityDateReqDto implements IDto {
    private LocalDate validTill;
}
