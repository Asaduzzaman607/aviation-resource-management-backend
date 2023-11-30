package com.digigate.engineeringmanagement.common.service.impl;


import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.converter.UserConverter;
import com.digigate.engineeringmanagement.common.entity.Role;
import com.digigate.engineeringmanagement.common.entity.User;
import com.digigate.engineeringmanagement.common.entity.erpDataSync.Employee;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.payload.projection.EmployeeProjection;
import com.digigate.engineeringmanagement.common.payload.request.LoginNameChangePayload;
import com.digigate.engineeringmanagement.common.payload.request.PasswordResetPayload;
import com.digigate.engineeringmanagement.common.payload.request.UserPayload;
import com.digigate.engineeringmanagement.common.payload.request.UserSearchDto;
import com.digigate.engineeringmanagement.common.payload.response.CustomUserResponseDto;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.payload.response.UserViewModel;
import com.digigate.engineeringmanagement.common.payload.response.UsersResponseMetaDataDto;
import com.digigate.engineeringmanagement.common.repository.UserRepository;
import com.digigate.engineeringmanagement.common.service.EmailService;
import com.digigate.engineeringmanagement.common.service.RoleService;
import com.digigate.engineeringmanagement.common.service.UserService;
import com.digigate.engineeringmanagement.common.service.auth.AuthorizationService;
import com.digigate.engineeringmanagement.common.service.erpDataSync.EmployeeService;
import com.digigate.engineeringmanagement.common.util.NumberUtil;
import com.digigate.engineeringmanagement.common.util.StringUtil;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.UserProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.UsernameProjection;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.*;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final RoleService roleService;
    private static final String ORDER_BY_LOGIN = "id";
    private final EmployeeService employeeService;
    private final AuthorizationService authorizationService;
    private final EmailService emailService;
    Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);


    /**
     * Parameterized constructor
     * @param userRepository {@link UserRepository}
     * @param encoder        {@link PasswordEncoder}
     * @param roleService    {@link RoleService}
     * @param authorizationService {@link AuthorizationService}
     * @param emailService
     */
    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder encoder,
                           RoleService roleService,
                           EmployeeService employeeService,
                           AuthorizationService authorizationService, EmailService emailService) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.roleService = roleService;
        this.employeeService = employeeService;
        this.authorizationService = authorizationService;
        this.emailService = emailService;
    }

    /**
     * find by login or throw error
     *
     * @param login {@link String}
     * @return {@link User}
     */
    @Override
    public User findByLogin(String login) {
        if (StringUtils.isBlank(login)) {
            throw new EngineeringManagementServerException(
                    ErrorId.ID_IS_REQUIRED,
                    HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID));
        }
        return userRepository.findByLogin(login).orElseThrow(() -> {
            throw new EngineeringManagementServerException(
                    ErrorId.USER_NOT_EXISTS,
                    HttpStatus.NOT_FOUND,
                    MDC.get(ApplicationConstant.TRACE_ID));
        });
    }

    /**
     * save an entity or throw error
     *
     * @param userPayload {@link UserPayload}
     * @return {@link UserViewModel}
     */
    @Override
    public UserViewModel saveUser(UserPayload userPayload) {
        userPayload.setIsActive(Boolean.TRUE);
        return saveOrUpdateUser(userPayload, null);
    }

    /**
     * update an entity or throw error
     *
     * @param userPayload {@link UserPayload}
     * @param id          {@link Long}
     * @return {@link Long}
     */
    @Override
    public Long updateUser(UserPayload userPayload, Long id) {

        authorizationService.validateSuperAdmin();

        if (Objects.isNull(id)) {
            throw new EngineeringManagementServerException(
                    ErrorId.ID_IS_REQUIRED,
                    HttpStatus.NOT_FOUND,
                    MDC.get(ApplicationConstant.TRACE_ID));
        }

        validateLoginWhileEdit(userPayload.getLogin(), id);

        Optional<User> userEntity = userRepository.findById(id);

        if (userEntity.isEmpty()) {
            throw new EngineeringManagementServerException(
                    ErrorId.USER_NOT_EXISTS,
                    HttpStatus.NOT_FOUND,
                    MDC.get(ApplicationConstant.TRACE_ID));
        }

        User currentUser = userEntity.get();
        Role role = roleService.findById(userPayload.getRoleId());
        currentUser.setLogin(userPayload.getLogin());
        currentUser.setRole(role);
        userRepository.save(currentUser);
        return currentUser.getId();
    }

    private void validateLoginWhileEdit(String login, Long id) {
        Optional<User> userWithSameLoginName = userRepository.findByLoginAndIdNot(login, id);

        if (userWithSameLoginName.isPresent()) {
            throw new EngineeringManagementServerException(
                    ErrorId.LOGIN_NAME_EXISTS,
                    HttpStatus.CONFLICT,
                    MDC.get(ApplicationConstant.TRACE_ID)
            );
        }
    }

    private synchronized void validateEmployeeId(Long employeeId) {
        List<User> userWithEmployeeId = userRepository.findByEmployeeIdAndIsActiveTrue(employeeId);

        if (CollectionUtils.isNotEmpty(userWithEmployeeId)) {
            throw new EngineeringManagementServerException(
                    ErrorId.USER_EXISTS_WITH_THIS_EMPLOYEE_ID,
                    HttpStatus.CONFLICT,
                    MDC.get(ApplicationConstant.TRACE_ID)
            );
        }
    }

    private UserViewModel saveOrUpdateUser(UserPayload userPayload, Long id) {
        authorizationService.validateSuperAdmin();
        Optional<User> optionalUser = userRepository.findByLogin(userPayload.getLogin());

        if (Objects.isNull(id) && optionalUser.isPresent()) {
            throw new EngineeringManagementServerException(
                    ErrorId.USER_EXISTS_WITH_THIS_NAME,
                    HttpStatus.NOT_FOUND,
                    MDC.get(ApplicationConstant.TRACE_ID));
        }

        User user = new User();
        Employee employee = employeeService.findById(userPayload.getEmployeeId());
        user.setEmployee(employee);
        UserConverter.convertToUserFromPayload(user, userPayload);
        String password = StringUtil.generateRandomString(PASSWORD_LENGTH);
        user.setPassword(encoder.encode(password));

        synchronized (userPayload.getEmployeeId()) {
            validateEmployeeId(userPayload.getEmployeeId());
            try {
                Role role = roleService.findById(userPayload.getRoleId());
                user.setRole(role);
                if(Objects.nonNull(employee.getEmail())){
                    sendPasswordMail(password, employee.getEmail());
                }
                return saveUser(user);
            } catch (EngineeringManagementServerException exception) {
                throw exception;
            } catch (Exception exception) {
                throw new EngineeringManagementServerException(
                        ErrorId.FAILED_TO_SAVE_USER,
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        MDC.get(ApplicationConstant.TRACE_ID));
            }
        }
    }

    /**
     * find an entity by id or throw error
     *
     * @param id {@link Long}
     * @return {@link User}
     */
    @Override
    public User findUserById(Long id) {
        if (Objects.isNull(id)) {
            throw new EngineeringManagementServerException(
                    ErrorId.ID_IS_REQUIRED,
                    HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID));
        }
        return userRepository.findById(id).orElseThrow(() -> {
            throw new EngineeringManagementServerException(
                    ErrorId.USER_NOT_EXISTS,
                    HttpStatus.NOT_FOUND,
                    MDC.get(ApplicationConstant.TRACE_ID));
        });
    }

    private void throwIDisNotPresentException() throws EngineeringManagementServerException {
        throw new EngineeringManagementServerException(
                ErrorId.ID_IS_REQUIRED,
                HttpStatus.BAD_REQUEST,
                MDC.get(ApplicationConstant.TRACE_ID)
        );
    }

    @Override
    public UserViewModel getSingeUser(Long id) {
        if (Objects.isNull(id)) {
            throwIDisNotPresentException();
        }

        return userRepository.findByUserId(id).orElseThrow(() -> {
            throw new EngineeringManagementServerException(
                    ErrorId.USER_NOT_EXISTS,
                    HttpStatus.NOT_FOUND,
                    MDC.get(ApplicationConstant.TRACE_ID));
        });
    }



    @Override
    public Long resetPassword(Long id) {
        authorizationService.validateSuperAdmin();
        User user = userRepository.findById(id)
                .orElseThrow(() -> EngineeringManagementServerException.notFound(ErrorId.USER_NOT_EXISTS));

        String password = StringUtil.generateRandomString(PASSWORD_LENGTH);
        user.setPassword(encoder.encode(password));
        logger.info("SYS GENERATED PASSWORD: " + password);
        userRepository.save(user);
        sendPasswordMail(password, user.getEmployee().getEmail());
        return user.getId();
    }

    private User getUserById(Long id) throws EngineeringManagementServerException {
        return userRepository.findById(id)
                .orElseThrow(() -> EngineeringManagementServerException.notFound(ErrorId.USER_NOT_EXISTS));
    }

    @Override
    public Long changePassword(PasswordResetPayload payload, Long id) {
        User user = getUserById(id);
        authorizationService.validateUserId(user.getId());

        if (!encoder.matches(payload.getPreviousPassword(), user.getPassword())) {
            throw new EngineeringManagementServerException(
                    ErrorId.USER_PASSWORD_DOES_NOT_MATCH,
                    HttpStatus.NOT_FOUND,
                    MDC.get(ApplicationConstant.TRACE_ID));
        }

        user.setPassword(encoder.encode(payload.getNewPassword()));
        userRepository.save(user);
        return user.getId();
    }

    @Override
    public Long changeLoginName(LoginNameChangePayload payload, Long id) {
        validateLoginWhileEdit(payload.getLogin(), id);
        User user = getUserById(id);
        authorizationService.validateUserId(user.getId());
        user.setLogin(payload.getLogin());
        userRepository.save(user);
        return user.getId();
    }


    /**
     * find entity detail or throw error
     *
     * @param id {@link Long}
     * @return {@link User}
     */
    @Override
    public UserViewModel findUserDetailById(Long id) {
        return UserConverter.convertToUserViewModel(findUserById(id));
    }

    /**
     * search entities by criteria
     *
     * @param searchDto {@link UserSearchDto}
     * @param page      {@link Integer}
     * @param size      {@link Integer}
     * @return {@link PageData}
     */
    @Override
    public PageData searchBySearchCriteria(UserSearchDto searchDto, Integer page, Integer size) {
        Sort sortUser = Sort.by(Sort.Direction.ASC, ORDER_BY_LOGIN);
        page = NumberUtil.getValidPageNumber(page);
        size = NumberUtil.getValidPageSize(size);
        Pageable pageable = PageRequest.of(page, size, sortUser);
        Page<UserViewModel> userViewModelPage = userRepository
                .findBySearchCriteria(searchDto.getLogin(),searchDto.getName(),
                        searchDto.getRoleId(), searchDto.getIsActive(), pageable);
        return new PageData(userViewModelPage.getContent(), userViewModelPage.getTotalPages(),
                page + 1, userViewModelPage.getTotalElements());

    }

    /**
     * find all active entity
     *
     * @return {@link List<UserViewModel>}
     */
    @Override
    public List<UserViewModel> findAllUser() {
        return userRepository.findAllUser();
    }

    /**
     * update activate status
     *
     * @param id       {@link Long}
     * @param isActive {@link Boolean}
     * @return {@link PageData}
     */
    @Override
    public void updateActiveStatus(Long id, Boolean isActive) {
        if (Objects.isNull(id)) {
            throw new EngineeringManagementServerException(
                    ErrorId.ID_IS_REQUIRED,
                    HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID));
        }
        User user = findUserById(id);
        if (user.getIsActive() == isActive) {
            throw EngineeringManagementServerException.badRequest(ErrorId.ONLY_TOGGLE_VALUE_ACCEPTED);
        }
        user.setIsActive(isActive);
        saveUser(user);
    }

    /**
     * Single user projection
     *
     * @param id {@link Long}
     * @return responding user projection
     */
    @Override
    public UsernameProjection findUsernameById(Long id) {
        return userRepository.findUserById(id);
    }

    /**
     * List of user projection
     *
     * @param idList {@link Long}
     * @return responding user projection
     */
    @Override
    public Set<UsernameProjection> findUsernameByIdList(Set<Long> idList) {
        return userRepository.findUserByIdIn(idList);
    }

    @Override
    public UsersResponseMetaDataDto getAllUser() {
        List<UsernameProjection> userList = userRepository.findByIsActiveTrue();
        Set<Long> empIds = userList.stream().map(UsernameProjection::getEmployeeId).collect(Collectors.toSet());
        Map<Long, EmployeeProjection> employeeProjectionMap = employeeService.findByIdIn(empIds).stream()
                .collect(Collectors.toMap(EmployeeProjection::getId, Function.identity()));
        List<List<String>> userResponseList = userList.stream().map(user -> convertToResponseDto(employeeProjectionMap.get(user.getEmployeeId()), user))
                .collect(Collectors.toList());
        return mappingKeyWithResponse(userResponseList);
    }

    private UsersResponseMetaDataDto mappingKeyWithResponse(List<List<String>> users) {
        Map<String, Integer> keyMap = new HashMap<>();
        keyMap.put(DESIGNATION, NUMBERS.ZERO);
        keyMap.put(DEPARTMENT, NUMBERS.ONE);
        keyMap.put(USER, NUMBERS.TWO);
        keyMap.put(EMPLOYEE, NUMBERS.THREE);
        keyMap.put(LOGIN, NUMBERS.FOUR);
        keyMap.put(SECTION, NUMBERS.FIVE);
        return UsersResponseMetaDataDto.builder().key(keyMap).list(users).build();
    }

    private List<String> convertToResponseDto(EmployeeProjection employeeProjection, UsernameProjection user) {
        CustomUserResponseDto responseDto = new CustomUserResponseDto();
        if (Objects.nonNull(employeeProjection)) {
            responseDto.setDesignationId(employeeProjection.getDesignationId());
            responseDto.setSectionId(employeeProjection.getDesignationSectionId());
            responseDto.setDepartmentId(employeeProjection.getDesignationSectionDepartmentId());
        }
        responseDto.setEmployeeId(user.getEmployeeId());
        responseDto.setUserId(user.getId());
        responseDto.setLogIn(user.getLogin());
        return responseDto.populateAsList();
    }

    public Set<UserProjection> findByIdIn(Set<Long> idList) {
        return userRepository.findByIdIn(idList);
    }

    private UserViewModel saveUser(User user) {
        try {
            userRepository.save(user);
            return UserConverter.convertToUserViewModel(user);
        } catch (Exception exception) {
            throw new EngineeringManagementServerException(
                    ErrorId.FAILED_TO_SAVE_USER,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    MDC.get(ApplicationConstant.TRACE_ID));
        }
    }

    public List<UsernameProjection> findByEmployeeIdIn(Set<Long> idList) {
        return userRepository.findByEmployeeIdIn(idList);
    }

    private void sendPasswordMail(String password, String to) {
        String subject = "Password created successfully!";
        emailService.sendEmail(Map.of("password", password), subject, ApplicationConstant.NEW_PASSWORD_TEMPLATE,
                Collections.singletonList(to));
    }

}
