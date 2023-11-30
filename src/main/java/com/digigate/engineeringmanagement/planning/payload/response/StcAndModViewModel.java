package com.digigate.engineeringmanagement.planning.payload.response;
import com.digigate.engineeringmanagement.planning.constant.TaskStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StcAndModViewModel {
    public String ata;
    public String modNo;
    public String description;
    public String refDoc;
    private LocalDate doneDate;
    private Double doneHour;
    private Integer doneCycle;
    private LocalDate dueDate;
    private Double dueHour;
    private Integer dueCycle;
    private TaskStatusEnum status;
    private String remark;
}
