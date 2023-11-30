package com.digigate.engineeringmanagement.configurationmanagement.dto.request.configuration;

import com.digigate.engineeringmanagement.common.payload.IDto;
import lombok.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyDto implements IDto {
    @NotBlank
    @Length(max = 50)
    private String companyName;
    private String addressLineOne;
    private String addressLineTwo;
    private String addressLineThree;
    private String phone;
    private String fax;
    private String email;
    private String contactPerson;
    @NonNull
    private Long baseCurrencyId;
    @NonNull
    private Long localCurrencyId;
    private String shipmentAddressOne;
    private String shipmentAddressTwo;
    private String shipmentAddressThree;
    private String companyUrl;
    private String companyLogo;
    @NonNull
    private Long cityId;
}
