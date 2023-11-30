package com.digigate.engineeringmanagement.planning.service;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.entity.erpDataSync.Employee;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.common.service.AbstractService;
import com.digigate.engineeringmanagement.common.service.erpDataSync.EmployeeService;
import com.digigate.engineeringmanagement.planning.entity.Signature;
import com.digigate.engineeringmanagement.planning.payload.request.SignatureDto;
import com.digigate.engineeringmanagement.planning.payload.request.SignatureSearchDto;
import com.digigate.engineeringmanagement.planning.payload.response.SignatureViewModel;
import com.digigate.engineeringmanagement.planning.repository.SignatureRepository;
import org.slf4j.MDC;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Signature service
 *
 * @author ashinisingha
 */
@Service
public class SignatureService extends AbstractService<Signature, SignatureDto> {
    private final EmployeeService employeeService;
    private final SignatureRepository signatureRepository;

    /**
     * Autowired constructor
     *
     * @param repository                        {@link AbstractRepository<Signature> }
     * @param employeeService                   {@link EmployeeService}
     * @param signatureRepository               {@link SignatureRepository}
     */
    public SignatureService(AbstractRepository<Signature> repository, EmployeeService employeeService, SignatureRepository signatureRepository) {
        super(repository);
        this.employeeService = employeeService;
        this.signatureRepository = signatureRepository;
    }

    /**
     * This method is responsible for searching signature
     *
     * @param signatureSearchDto        {@link SignatureSearchDto}
     * @param pageable                  {@link Pageable}
     * @return                          {@link Page<SignatureViewModel> }
     */
    public Page<SignatureViewModel> searchSignature(SignatureSearchDto signatureSearchDto, Pageable pageable){
        return signatureRepository.findSignatureBySearchCriteria(
          signatureSearchDto.getEmployeeName(),
          signatureSearchDto.getAuthNo(),
          signatureSearchDto.getIsActive(),
          pageable
        );
    }

    /**
     * this method is responsible for getting all active signature list
     *
     * @return          {@link List<Signature> }
     */
    public List<SignatureViewModel> getAllActiveList(){
        List<Signature> signatureList = signatureRepository.getAllByIsActiveTrue();
        return  signatureList.stream().map( signature ->
                convertToResponseDto(signature)  ).collect(Collectors.toList());
    }


    /**
     * This method is responsible for converting Signature entity to Response view model
     *
     * @param signature                     {@link Signature}
     * @return signatureViewModel           {@link SignatureViewModel}
     */
    @Override
    protected SignatureViewModel convertToResponseDto(Signature signature) {
        Employee employee = employeeService.findById(signature.getEmployee().getId());
        SignatureViewModel signatureViewModel = new SignatureViewModel();
        signatureViewModel.setId(signature.getId());
        signatureViewModel.setEmployeeId(employee.getId());
        signatureViewModel.setEmployeeName(employee.getName());
        signatureViewModel.setAuthNo(signature.getAuthNo());
        signatureViewModel.setIsActive(signature.getIsActive());
        return signatureViewModel;
    }

    /**
     * this method is responsible for converting signature request Dto to Signature entity
     *
     * @param signatureDto {@link SignatureDto}
     * @return {@link Signature}
     */
    @Override
    protected Signature convertToEntity(SignatureDto signatureDto) {
        return saveAndUpdateCommon(signatureDto, new Signature());
    }

    /**
     * this method is responsible update an signature entity
     *
     * @param dto                   {@link SignatureDto}
     * @param entity                {@link Signature}
     * @return                      {@link Signature}
     */
    @Override
    protected Signature updateEntity(SignatureDto dto, Signature entity) {
        return saveAndUpdateCommon(dto, entity);
    }

    private Signature saveAndUpdateCommon(SignatureDto signatureDto, Signature signature){
        if(Objects.nonNull(signature.getId())){
            if(!signatureDto.getAuthNo().equals(signature.getAuthNo())){
                if(signatureRepository.existsByAuthNo(signatureDto.getAuthNo())){
                    throw new EngineeringManagementServerException(
                            ErrorId.SIGNATURE_AUTH_NAME_ALREADY_EXISTS,
                            HttpStatus.BAD_REQUEST,
                            MDC.get(ApplicationConstant.TRACE_ID)
                    );
                }
            }
        }else{
            if(signatureRepository.existsByAuthNo(signatureDto.getAuthNo())){
                throw new EngineeringManagementServerException(
                        ErrorId.SIGNATURE_AUTH_NAME_ALREADY_EXISTS,
                        HttpStatus.BAD_REQUEST,
                        MDC.get(ApplicationConstant.TRACE_ID)
                );
            }
            signature.setIsActive(true);
        }
        signature.setEmployee( employeeService.findById(signatureDto.getEmployeeId()) );
        signature.setAuthNo(signatureDto.getAuthNo());
        return signature;
    }
}
