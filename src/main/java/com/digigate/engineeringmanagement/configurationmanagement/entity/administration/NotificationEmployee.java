package com.digigate.engineeringmanagement.configurationmanagement.entity.administration;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.common.payload.IDto;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "notification_employees")
public class NotificationEmployee extends AbstractDomainBasedEntity implements IDto {
    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @ManyToOne
    @JoinColumn(name = "notification_setting_id")
    private NotificationSetting notificationSetting;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NotificationEmployee)) return false;
        if (!super.equals(o)) return false;

        NotificationEmployee that = (NotificationEmployee) o;

        if (getEmployeeId() != null ? !getEmployeeId().equals(that.getEmployeeId()) : that.getEmployeeId() != null)
            return false;
        return getNotificationSetting() != null ? getNotificationSetting().equals(that.getNotificationSetting()) : that.getNotificationSetting() == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getEmployeeId() != null ? getEmployeeId().hashCode() : 0);
        result = 31 * result + (getNotificationSetting() != null ? getNotificationSetting().hashCode() : 0);
        return result;
    }
}