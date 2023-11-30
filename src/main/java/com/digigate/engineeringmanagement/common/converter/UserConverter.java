package com.digigate.engineeringmanagement.common.converter;

import com.digigate.engineeringmanagement.common.entity.User;
import com.digigate.engineeringmanagement.common.payload.request.UserPayload;
import com.digigate.engineeringmanagement.common.payload.response.UserViewModel;
import com.digigate.engineeringmanagement.common.util.CaseConverter;
import org.apache.commons.lang3.BooleanUtils;

import java.util.Objects;

public class UserConverter {

    /**
     * convert UserPayload to user
     *
     * @param user        {@link User}
     * @param userPayload {@link UserPayload}
     * @return user {@link User}
     */
    public static User convertToUserFromPayload(User user, UserPayload userPayload) {
        user.setLogin(userPayload.getLogin());
//        user.setEmployeeId(userPayload.getEmployeeId());
        user.setIsActive(BooleanUtils.toBoolean(userPayload.getIsActive()));
        return user;
    }

    /**
     * convert UserPayload to user
     *
     * @param user {@link User}
     * @return {@link UserViewModel}
     */
    public static UserViewModel convertToUserViewModel(User user) {
        UserViewModel userViewModel = new UserViewModel();
        userViewModel.setId(user.getId());
        userViewModel.setLogin(user.getLogin());
        addRole(user, userViewModel);
        userViewModel.setCreatedAt(user.getCreatedAt());
        userViewModel.setIsActive(BooleanUtils.toBoolean(user.getIsActive()));
        return userViewModel;
    }

    private static void addRole(User user, UserViewModel userViewModel) {
        if(Objects.isNull(user.getRole())) {
            return;
        }
        userViewModel.setRoleId(user.getRole().getId());
        userViewModel.setRoleName(user.getRole().getName());
    }

}
