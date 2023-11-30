package com.digigate.engineeringmanagement.storemanagement.payload.response;

import lombok.*;

import javax.validation.constraints.Size;

@Data
public class StoreSerialIdNoDto {
    private Long storeSerialId;
    private Long serialId;
    private String serialNo;
    private Double price;
    private Long uomId;
    @Size(max = 200)
    private String uomCode;

    public StoreSerialIdNoDto(Long storeSerialId, Long serialId, String serialNo, Double price) {
        this.storeSerialId = storeSerialId;
        this.serialId = serialId;
        this.serialNo = serialNo;
        this.price = price;

    }

    public StoreSerialIdNoDto(Long storeSerialId, Long serialId, String serialNo, Double price, Long uomId, String uomCode) {
        this.storeSerialId = storeSerialId;
        this.serialId = serialId;
        this.serialNo = serialNo;
        this.price = price;
        this.uomId = uomId;
        this.uomCode = uomCode;
    }
}
