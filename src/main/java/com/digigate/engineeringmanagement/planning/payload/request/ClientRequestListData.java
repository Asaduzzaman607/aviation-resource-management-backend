package com.digigate.engineeringmanagement.planning.payload.request;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ClientRequestListData<E> {
    @Valid
    private List<E> data;
}