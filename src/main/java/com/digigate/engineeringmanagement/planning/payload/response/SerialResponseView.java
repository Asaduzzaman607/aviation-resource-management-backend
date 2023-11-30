package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class SerialResponseView {
    Long serialId;
    String serialNo;
}
