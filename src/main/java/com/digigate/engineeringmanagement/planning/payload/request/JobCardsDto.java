package com.digigate.engineeringmanagement.planning.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JobCardsDto {

    private Long workPackageId;

    private Long jobCardsId;

    @NotNull
    private String jobCategory;

    private Integer total;
    private Integer completed;
    private Integer deferred;
    private Integer withDrawn;
    private String remark;
}
