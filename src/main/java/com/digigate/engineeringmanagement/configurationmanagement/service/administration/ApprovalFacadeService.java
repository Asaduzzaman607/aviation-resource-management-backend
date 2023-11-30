package com.digigate.engineeringmanagement.configurationmanagement.service.administration;

import com.digigate.engineeringmanagement.common.payload.projection.EmployeeProjection;
import com.digigate.engineeringmanagement.common.payload.response.CustomUserResponseDto;
import com.digigate.engineeringmanagement.common.service.erpDataSync.EmployeeService;
import com.digigate.engineeringmanagement.common.service.impl.UserServiceImpl;
import com.digigate.engineeringmanagement.configurationmanagement.entity.ConfigSubmoduleItem;
import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.ApprovalEmployee;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.UsernameProjection;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ApprovalFacadeService {

    private final ApprovalEmployeeService approvalEmployeeService;
    private final ISubModuleItemService configSubmoduleItemService;
    private final UserServiceImpl userService;
    private final EmployeeService employeeService;


    public ApprovalFacadeService(ApprovalEmployeeService approvalEmployeeService, ISubModuleItemService configSubmoduleItemService,
                                 UserServiceImpl userService, EmployeeService employeeService) {
        this.approvalEmployeeService = approvalEmployeeService;
        this.configSubmoduleItemService = configSubmoduleItemService;
        this.userService = userService;
        this.employeeService = employeeService;
    }
    /*
      *save approvalEmployees object in database
     */
    public void saveApprovalEmployee(List<ApprovalEmployee> approvalEmployees) {
        approvalEmployeeService.saveAll(approvalEmployees);
    }
    /*
      *finding all existingNotificationEmployees
     */
    public Set<Long> findAllExistingApprovalEmployees(Long approvalSettingId, Set<Long> employeeIds) {
        return approvalEmployeeService.findAllExistingApprovalEmployees(approvalSettingId, employeeIds).stream()
                .map(ApprovalEmployee::getEmployeeId)
                .collect(Collectors.toSet());
    }

    public List<ConfigSubmoduleItem> getAllSubModuleItemsByIdIn(Set<Long> ids) {
        return configSubmoduleItemService.getAllSubModuleItemsByIdIn(ids);
    }

    public ConfigSubmoduleItem findSubmoduleItemById(Long id) {
        return configSubmoduleItemService.findById(id);
    }

    public Set<Long> getAllEmployeeIdsByApprovalSettingId(Long id) {
        return approvalEmployeeService.getAllEmployeeIdsByApprovalSettingId(id);
    }

    public void deleteAllByApprovalSettingIdAndEmployeeIdIn(Long id, Set<Long> employeesId) {
        approvalEmployeeService.deleteAllByApprovalSettingIdAndEmployeeIdIn(id,employeesId);
    }

    public List<CustomUserResponseDto> getAllUserByApprovalSettingId(Long approvalSettingId,
                                                                               Long departmentId, Long sectionId,
                                                                               Long designationId) {
        Set<Long> approvedUserIds = approvalEmployeeService.getAllEmployeeIdsByApprovalSettingId(
                approvalSettingId);

        Set<UsernameProjection> userNameDtos = userService.findUsernameByIdList(approvedUserIds);

        Set<Long> employeeIds = userNameDtos.stream().map(UsernameProjection::getEmployeeId)
                .filter(Objects::nonNull).collect(Collectors.toSet());

        Map<Long, EmployeeProjection> employeeProjectionMap = employeeService.findByIdIn(employeeIds).stream()
                .collect(Collectors.toMap(EmployeeProjection::getId, Function.identity()));

        Set<Long> filteredEmployees = getFilteredEmployeeIds(departmentId, sectionId, designationId, employeeProjectionMap);

        Set<UsernameProjection> filteredUsers = userNameDtos.stream().filter(usernameDto -> filteredEmployees.contains(usernameDto.getEmployeeId()))
                        .collect(Collectors.toSet());

        if (isFilterNotApplicable(departmentId, sectionId, designationId)) {
            Set<UsernameProjection> employeeNull = userNameDtos.stream().filter(usernameProjection ->
                    Objects.isNull(usernameProjection.getEmployeeId())).collect(Collectors.toSet());
            filteredUsers.addAll(employeeNull);
        }

        return filteredUsers.stream().map(usernameProjection ->
                convertToResponse(usernameProjection, employeeProjectionMap.get(usernameProjection.getEmployeeId())))
                .collect(Collectors.toList());
    }

    private CustomUserResponseDto convertToResponse(UsernameProjection usernameProjection, EmployeeProjection employeeProjection) {
        CustomUserResponseDto responseDto = new CustomUserResponseDto();
        if (Objects.nonNull(employeeProjection)) {
            responseDto.setDepartmentId(employeeProjection.getDesignationSectionDepartmentId());
            responseDto.setSectionId(employeeProjection.getDesignationSectionId());
            responseDto.setDesignationId(employeeProjection.getDesignationId());
            responseDto.setEmployeeId(employeeProjection.getId());
        }
        if (Objects.nonNull(usernameProjection)) {
            responseDto.setLogIn(usernameProjection.getLogin());
            responseDto.setUserId(usernameProjection.getId());
        }
        return responseDto;
    }

    private Set<Long> getFilteredEmployeeIds(Long departmentId, Long sectionId, Long designationId,
                                             Map<Long, EmployeeProjection> employeeProjectionMap) {
       if (employeeProjectionMap.isEmpty()){
           return Collections.emptySet();
       }
        if (isFilterNotApplicable(departmentId, sectionId, designationId)) {
            return employeeProjectionMap.keySet();
        }
        return employeeProjectionMap.keySet().stream()
                .filter(employeeId -> {
                    EmployeeProjection employeeProjection = employeeProjectionMap.get(employeeId);
                    return Objects.equals(employeeProjection.getDesignationId(), designationId)
                            || Objects.equals(employeeProjection.getDesignationSectionId(), sectionId)
                            || Objects.equals(employeeProjection.getDesignationSectionDepartmentId(), departmentId);
                })
                .collect(Collectors.toSet());
    }

    private static boolean isFilterNotApplicable(Long departmentId, Long sectionId, Long designationId) {
        return Objects.isNull(departmentId) && Objects.isNull(sectionId) && Objects.isNull(designationId);
    }
}
