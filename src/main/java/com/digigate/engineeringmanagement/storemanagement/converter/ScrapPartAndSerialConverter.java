package com.digigate.engineeringmanagement.storemanagement.converter;

import com.digigate.engineeringmanagement.storemanagement.entity.scrap.StoreScrapPart;
import com.digigate.engineeringmanagement.storemanagement.entity.scrap.StoreScrapPartSerial;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.StorePartSerial;

public class ScrapPartAndSerialConverter {

    public static StoreScrapPartSerial convertToEntity(StoreScrapPart storeScrapPart,
                                                       StorePartSerial storePartSerial,
                                                       Integer quantity) {
        StoreScrapPartSerial storeScrapPartSerial = new StoreScrapPartSerial();
        storeScrapPartSerial.setStoreScrapPart(storeScrapPart);
        storeScrapPartSerial.setStorePartSerial(storePartSerial);
        storeScrapPartSerial.setQuantity(quantity);
        return storeScrapPartSerial;
    }
}
