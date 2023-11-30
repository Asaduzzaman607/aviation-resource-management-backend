package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

import javax.validation.constraints.NotNull;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobCardsViewModel {

    private Long jobCardsId;

    private String jobCategory;

    private Integer total;
    private Integer completed;
    private Integer deferred;
    private Integer withDrawn;
    private String remark;
}
