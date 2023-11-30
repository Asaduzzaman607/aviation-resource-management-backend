package com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration;

import com.digigate.engineeringmanagement.storemanagement.constant.RemarkType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.EMPTY_STRING;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "parts_remarks")
public class PartRemark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "action_part_id", nullable = false)
    private Long itemId;

    @Column(name = "parent_id", nullable = false)
    private Long parentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "remark_type", nullable = false)
    private RemarkType remarkType;

    @Column(name = "remark", nullable = false, length = 8000)
    private String remark;

    @CreationTimestamp
    @Column(name = "create_date", updatable = false)
    private LocalDateTime createdAt;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof PartRemark)) return false;
        return Objects.nonNull(this.getId()) && Objects.equals(this.getId(), (((PartRemark) object).getId()));
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }
    public static PartRemark withEmptyRemark() {
        return PartRemark.builder().remark(EMPTY_STRING).build();
    }
}
