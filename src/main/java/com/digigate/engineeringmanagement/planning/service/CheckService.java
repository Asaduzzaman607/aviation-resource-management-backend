package com.digigate.engineeringmanagement.planning.service;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.planning.entity.Check;
import com.digigate.engineeringmanagement.planning.payload.request.CheckDto;
import com.digigate.engineeringmanagement.planning.payload.request.CheckSearchDto;
import com.digigate.engineeringmanagement.planning.repository.CheckRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.MDC;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
/**
 * Check Service
 *
 * @author Ashraful
 */
@Service
public class CheckService extends AbstractSearchService<Check, CheckDto, CheckSearchDto> {
    private static final String CHECK_TITLE = "title";
    private static final String CHECK_IS_ACTIVE = "isActive";
    private CheckRepository checkRepository;

    /**
     * Autowired constructor
     *
     * @param repository {@link AbstractRepository}
     * @param checkRepository {@link CheckRepository}
     */
    public CheckService(AbstractRepository<Check> repository, CheckRepository checkRepository) {
        super(repository);
        this.checkRepository = checkRepository;
    }

    @Override
    protected Check convertToResponseDto(Check check) {
        return check;
    }

    @Override
    protected Check convertToEntity(CheckDto checkDto) {
        this.validateCheckTitle(checkDto);
        return Check.builder()
                .title(checkDto.getTitle())
                .description(checkDto.getDescription())
                .build();
    }

    /**
     * This method responsible for checking CheckTitle uniqueness
     *
     * @param checkDto {@link CheckDto}
     */
    private void validateCheckTitle(CheckDto checkDto) {
        if (this.checkRepository.findIdByTitle(
                checkDto.getTitle()).isPresent()) {
            throw new EngineeringManagementServerException(
                    ErrorId.CHECK_TITLE_ALREADY_EXIST, HttpStatus.UNPROCESSABLE_ENTITY,
                    MDC.get(ApplicationConstant.TRACE_ID)
            );
        }
    }

    @Override
    protected Check updateEntity(CheckDto dto, Check check) {

        if (!check.getTitle().equals(dto.getTitle())) {
            validateCheckTitle(dto);
        }
        check.setTitle(dto.getTitle());
        check.setDescription(dto.getDescription());
        return check;
    }

    @Override
    protected Specification<Check> buildSpecification(CheckSearchDto searchDto) {
        CustomSpecification<Check> customSpecification = new CustomSpecification<>();
        return Specification.where(customSpecification.likeSpecificationAtRoot(searchDto.getTitle(),
                CHECK_TITLE).and(customSpecification.equalSpecificationAtRoot(searchDto.getIsActive(),
                CHECK_IS_ACTIVE)));
    }
}
