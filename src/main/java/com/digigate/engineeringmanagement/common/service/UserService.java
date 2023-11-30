package com.digigate.engineeringmanagement.common.service;


import com.digigate.engineeringmanagement.common.entity.User;
import com.digigate.engineeringmanagement.common.payload.request.LoginNameChangePayload;
import com.digigate.engineeringmanagement.common.payload.request.PasswordResetPayload;
import com.digigate.engineeringmanagement.common.payload.request.UserPayload;
import com.digigate.engineeringmanagement.common.payload.request.UserSearchDto;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.payload.response.UserViewModel;
import com.digigate.engineeringmanagement.common.payload.response.UsersResponseMetaDataDto;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.UsernameProjection;

import java.util.List;
import java.util.Set;

public interface UserService {

    User findByLogin(String login);

    UserViewModel saveUser(UserPayload userPayload);

    Long updateUser(UserPayload userPayload, Long id);

    User findUserById(Long id);

    UserViewModel findUserDetailById(Long id);

    PageData searchBySearchCriteria(UserSearchDto userUpdateDto, Integer page, Integer size);

    List<UserViewModel> findAllUser();

    void updateActiveStatus(Long id, Boolean isActive);

    UsernameProjection findUsernameById(Long id);

    Set<UsernameProjection> findUsernameByIdList(Set<Long> idList);

    UsersResponseMetaDataDto getAllUser();

    UserViewModel getSingeUser(Long id);

    Long resetPassword(Long id);

    Long changePassword(PasswordResetPayload payload, Long id);

    Long changeLoginName(LoginNameChangePayload payload, Long id);
}