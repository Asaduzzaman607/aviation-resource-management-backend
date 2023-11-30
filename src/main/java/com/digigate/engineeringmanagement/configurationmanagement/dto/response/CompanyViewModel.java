package com.digigate.engineeringmanagement.configurationmanagement.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyViewModel {
    private Long id;
    private String companyName;
    private String addressLineOne;
    private String addressLineTwo;
    private String addressLineThree;
    private String phone;
    private String fax;
    private String email;
    private String contactPerson;
    private Long baseCurrencyId;
    private String baseCurrency;
    private Long localCurrencyId;
    private String localCurrency;
    private String shipmentAddressOne;
    private String shipmentAddressTwo;
    private String shipmentAddressThree;
    private String companyUrl;
    private String companyLogo;
    private String cityName;
    private Long cityId;
    private String countryName;
    private Long countryId;
}
