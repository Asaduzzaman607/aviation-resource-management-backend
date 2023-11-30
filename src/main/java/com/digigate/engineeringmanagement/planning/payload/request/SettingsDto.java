package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.common.payload.IDto;
import com.digigate.engineeringmanagement.planning.constant.SettingsHeaderEnum;
import lombok.*;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * Settings dto
 *
 * @author Asifur Rahman
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettingsDto implements IDto {
    @Enumerated(EnumType.STRING)
    private SettingsHeaderEnum headerKey;
    private String headerValue;
}
