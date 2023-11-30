package com.digigate.engineeringmanagement.common.controller;


import com.digigate.engineeringmanagement.common.authentication.security.services.UserDetailsImpl;
import com.digigate.engineeringmanagement.common.authentication.service.RefreshTokenService;
import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.payload.request.LoginNameChangePayload;
import com.digigate.engineeringmanagement.common.payload.request.PasswordResetPayload;
import com.digigate.engineeringmanagement.common.payload.request.UserPayload;
import com.digigate.engineeringmanagement.common.payload.request.UserSearchDto;
import com.digigate.engineeringmanagement.common.payload.response.MessageResponse;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.payload.response.UserViewModel;
import com.digigate.engineeringmanagement.common.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private static final String ACTIVE_STATUS_CHANGED_SUCCESSFULLY_MESSAGE = "Active Status Changed Successfully";

    /**
     * Parameterized constructor
     *
     * @param userService         {@link UserService}
     * @param refreshTokenService {@link RefreshTokenService}
     */
    @Autowired
    public UserController(UserService userService, RefreshTokenService refreshTokenService) {
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
    }

    /**
     * An API endpoint to create new entity
     *
     * @param {@link UserPayload}
     * @return {@link ResponseEntity<UserViewModel>}
     */
    @PostMapping("/")
    public ResponseEntity<UserViewModel> registerUser(@Valid @RequestBody UserPayload userPayload) {
        return ResponseEntity.ok(userService.saveUser(userPayload));
    }

    /**
     * An API endpoint to update entity
     *
     * @param {@link UserPayload}
     * @return {@link ResponseEntity<UserViewModel>}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Long> updateUser(
            @Valid @RequestBody UserPayload userPayload, @PathVariable Long id) {
        return ResponseEntity.ok(userService.updateUser(userPayload, id));
    }

    @PutMapping("/reset-password/{id}")
    public ResponseEntity<Long> resetPassword(@PathVariable Long id) {
        return ResponseEntity.ok(userService.resetPassword(id));
    }

    @PutMapping("/change-password/{id}")
    public ResponseEntity<Long> changePassword(@Valid @RequestBody PasswordResetPayload payload,
                                               @PathVariable Long id) {
        return ResponseEntity.ok(userService.changePassword(payload, id));
    }

    @PutMapping("/change-login/{id}")
    public ResponseEntity<Long> changeLoginName(@Valid @RequestBody LoginNameChangePayload payload,
                                               @PathVariable Long id) {
        return ResponseEntity.ok(userService.changeLoginName(payload, id));
    }

    /**
     * An API endpoint to get entity detail by id
     *
     * @param {@link Long}
     * @return {@link ResponseEntity<UserViewModel>}
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserViewModel> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getSingeUser(id));
    }

    /**
     * An API endpoint to search entity by criteria
     *
     * @param searchDto {@link UserSearchDto}
     * @param page      {@link Integer}
     * @param size      {@link Integer}
     * @return {@link ResponseEntity<PageData>}
     */
    @PostMapping("/search")
    public ResponseEntity<PageData> searchUserWithCriteria(
            @RequestBody UserSearchDto searchDto,
            @RequestParam(defaultValue = ApplicationConstant.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(defaultValue = ApplicationConstant.DEFAULT_PAGE_SIZE) Integer size
    ) {
        return ResponseEntity.ok(userService.searchBySearchCriteria(searchDto, page, size));
    }

    /**
     * An API endpoint to get all active entity
     *
     * @return user list {@link UserViewModel}
     */
    @GetMapping("/")
    public ResponseEntity<List<UserViewModel>> findAllActiveUser() {
        return ResponseEntity.ok(userService.findAllUser());
    }

    /**
     * An API endpoint to login
     *
     * @return {@link ResponseEntity< MessageResponse >}
     */
    @PutMapping("/logout")
    public ResponseEntity<MessageResponse> logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        refreshTokenService.deleteByUserId(((UserDetailsImpl) authentication.getPrincipal()).getId());
        return ResponseEntity.ok(new MessageResponse(ApplicationConstant.SUCCESSFULLY_LOGOUT));
    }

    /**
     * An API endpoint to update active status
     *
     * @param id       {@link Long}
     * @param isActive {@link Boolean}
     * @return {@link ResponseEntity< MessageResponse >}
     */
    @PatchMapping("/{id}")
    public ResponseEntity<MessageResponse> updateActiveStatus(
            @PathVariable Long id, @RequestParam("isActive") Boolean isActive) {
        userService.updateActiveStatus(id, isActive);
        return ResponseEntity.ok(new MessageResponse(ACTIVE_STATUS_CHANGED_SUCCESSFULLY_MESSAGE));
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUser());
    }
}
