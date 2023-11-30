package com.digigate.engineeringmanagement.storemanagement.payload.response.scrap;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ScrapPartSerialViewModel {
    private Long id;
    private Long serialId;
    private String serialNo;
    private Integer quantity;
    private Boolean isActive;

    public void id(Long id) {
        this.id = id;
    }
}
