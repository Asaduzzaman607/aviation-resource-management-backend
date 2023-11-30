package com.digigate.engineeringmanagement.configurationmanagement.service.administration;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.common.service.AbstractService;
import com.digigate.engineeringmanagement.configurationmanagement.dto.projection.WorkFlowActionProjection;
import com.digigate.engineeringmanagement.configurationmanagement.dto.request.administration.ApprovalEmployeeDto;
import com.digigate.engineeringmanagement.configurationmanagement.entity.administration.ApprovalEmployee;
import com.digigate.engineeringmanagement.configurationmanagement.repository.administration.ApprovalEmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ApprovalEmployeeService extends AbstractService<ApprovalEmployee, ApprovalEmployeeDto> {

    private final ApprovalEmployeeRepository approvalEmployeeRepository;


    public ApprovalEmployeeService(AbstractRepository<ApprovalEmployee> approvalEmployeeRepository, ApprovalEmployeeRepository approvalEmployeeRepository1) {
        super(approvalEmployeeRepository);
        this.approvalEmployeeRepository = approvalEmployeeRepository1;
    }

    public Set<ApprovalEmployee> findAllExistingApprovalEmployees(Long approvalSettingId, Set<Long> employeeIds) {
        return approvalEmployeeRepository.findAllByApprovalSettingIdAndEmployeeIdInAndIsActiveTrue(approvalSettingId, employeeIds);
    }

    public List<WorkFlowActionProjection> findApprovedActionsForUser(Long subModuleId, Long employeeId) {
        return approvalEmployeeRepository.findApprovedActionsForUser(subModuleId,
            employeeId);
    }

    @Override
    protected ApprovalEmployeeDto convertToResponseDto(ApprovalEmployee approvalEmployee) {
         return ApprovalEmployeeDto.builder()
                 .employeeId(approvalEmployee.getEmployeeId())
                 .ApprovalSettingId(approvalEmployee.getApprovalSetting().getId())
                 .id(approvalEmployee.getId())
                 .build();
    }

    @Override
    protected ApprovalEmployee convertToEntity(ApprovalEmployeeDto approvalEmployeeDto) {
        return null; // No direct save method
    }

    @Override
    protected ApprovalEmployee updateEntity(ApprovalEmployeeDto dto, ApprovalEmployee entity) {
        return entity;
    }

    public void saveAll(List<ApprovalEmployee> employees) {
        approvalEmployeeRepository.saveAll(employees);
    }


   public Set<Long> getAllEmployeeIdsByApprovalSettingId(Long approvalSettingId){
        return  approvalEmployeeRepository
                .findAllByApprovalSettingIdAndIsActiveTrue(approvalSettingId)
                .stream()
                .map(ApprovalEmployee::getEmployeeId)
                .collect(Collectors.toSet());
   }

    public void deleteAllByApprovalSettingIdAndEmployeeIdIn(Long id, Set<Long> employeeIds){
        approvalEmployeeRepository.deleteAllByApprovalSettingIdAndEmployeeIdIn(id,employeeIds);
    }
}
