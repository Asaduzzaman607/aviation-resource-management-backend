package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class AmlDropdownViewModel {
    private Long amlId;
    private Integer pageNo;
    private Character alphabet;

    public AmlDropdownViewModel(Integer pageNo, Character alphabet) {
        this.pageNo = pageNo;
        this.alphabet = alphabet;
    }
}
