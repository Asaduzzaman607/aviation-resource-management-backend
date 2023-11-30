package com.digigate.engineeringmanagement.storemanagement.converter;

import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.StoreIssueItem;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.StoreIssueSerial;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.StorePartSerial;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.GrnAndSerialDto;

public class IssueSerialConverter {

    public static StoreIssueSerial convertToEntity(StoreIssueItem storeIssueItem, StorePartSerial storePartSerial, GrnAndSerialDto grnAndSerialDto) {
        StoreIssueSerial storeIssueSerial = new StoreIssueSerial();
        storeIssueSerial.setStoreIssueItem(storeIssueItem);
        storeIssueSerial.setStorePartSerial(storePartSerial);
        storeIssueSerial.setQuantity(grnAndSerialDto.getQuantity());
        storeIssueSerial.setGrnNo(grnAndSerialDto.getGrnNo());
        return storeIssueSerial;
    }
}
