package com.digigate.engineeringmanagement.storemanagement.controller.storeconfiguration;

import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.Currency;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storeconfiguration.CurrencyRequestDto;
import com.digigate.engineeringmanagement.storemanagement.service.storeconfiguration.CurrencyService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/store/currencies")
public class CurrencyController extends AbstractSearchController<Currency, CurrencyRequestDto, IdQuerySearchDto> {
    public CurrencyController(CurrencyService currencyService) {
        super(currencyService);
    }
}
