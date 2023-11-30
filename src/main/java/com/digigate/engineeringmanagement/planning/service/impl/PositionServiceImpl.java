package com.digigate.engineeringmanagement.planning.service.impl;

import com.digigate.engineeringmanagement.common.config.model.ExcelData;
import com.digigate.engineeringmanagement.common.config.model.ExcelDataResponse;
import com.digigate.engineeringmanagement.common.config.util.ExcelFileUtil;
import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.common.util.StringUtil;
import com.digigate.engineeringmanagement.planning.entity.Position;
import com.digigate.engineeringmanagement.planning.payload.request.PositionDto;
import com.digigate.engineeringmanagement.planning.payload.request.PositionSearchDto;
import com.digigate.engineeringmanagement.planning.repository.PositionRepository;
import com.digigate.engineeringmanagement.planning.service.PositionIService;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.PositionProjection;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.IS_ACTIVE_FIELD;

@Service
public class PositionServiceImpl extends AbstractSearchService<Position, PositionDto, PositionSearchDto> implements PositionIService {
    private final PositionRepository positionRepository;
    private final Environment environment;


    private static final String POSITION = "Position";
    private static final String ARM_EXCEL_POSITION = "arm.excel.upload.file.name.position";
    private static final String NAME = "Name";
    private static final String DESCRIPTION = "Description";

    /**
     * Autowired constructor
     *
     * @param repository         {@link AbstractRepository}
     * @param positionRepository {@link  PositionRepository}
     * @param environment        {@link Environment}
     */
    @Autowired
    public PositionServiceImpl(AbstractRepository<Position> repository,
                               PositionRepository positionRepository, Environment environment) {
        super(repository);
        this.positionRepository = positionRepository;
        this.environment = environment;
    }

    @Override
    protected PositionDto convertToResponseDto(Position position) {
        return PositionDto.builder()
                .positionId(position.getId())
                .name(position.getName())
                .description(position.getDescription())
                .build();
    }

    @Override
    protected Position convertToEntity(PositionDto positionDto) {
        validatePositionDto(positionDto, null);
        return convertDtoToEntity(positionDto, new Position());
    }

    @Override
    protected Position updateEntity(PositionDto dto, Position entity) {
        validatePositionDto(dto, entity.getId());
        convertDtoToEntity(dto, entity);
        return entity;
    }



    /**
     * This method is responsible for validate unique position name
     *
     * @param positionDto {@link PositionDto}
     */
    public void validatePositionDto(PositionDto positionDto, Long id) {
        Optional<Position> optionalPosition = positionRepository.findByName(positionDto.getName());
        if (optionalPosition.isEmpty()) {
            return;
        }
        Position position = optionalPosition.get();
        if (Objects.isNull(id)) {
            throw new EngineeringManagementServerException(ErrorId.UNIQUE_NAME_IS_REQUIRED,
                    HttpStatus.BAD_REQUEST, MDC.get(ApplicationConstant.TRACE_ID));
        } else if (!position.getId().equals(id)) {
            throw new EngineeringManagementServerException(ErrorId.UNIQUE_NAME_IS_REQUIRED,
                    HttpStatus.BAD_REQUEST, MDC.get(ApplicationConstant.TRACE_ID));
        }
    }

    /**
     * This method is responsible for converting Position Record to Entity
     *
     * @param entity {@link Position}
     * @param dto    {@link PositionDto}
     */
    public Position convertDtoToEntity(PositionDto dto, Position entity) {
        entity.setDescription(dto.getDescription());
        entity.setName(dto.getName());
        return entity;
    }

    /**
     * This method is responsible for uploading aircraft location data via Excel file
     *
     * @param file {@link MultipartFile}
     * @return {@link ExcelDataResponse}
     * @throws IOException
     */
    @Override
    public ExcelDataResponse uploadExcel(MultipartFile file) {
        ExcelData excelData = ExcelFileUtil
                .getExcelDataFromSheet(file, environment.getProperty(ARM_EXCEL_POSITION), POSITION);

        if (CollectionUtils.isNotEmpty(excelData.getErrorMessages())) {
            return ExcelFileUtil.prepareErrorResponse(excelData.getErrorMessages());
        }
        List<String> errorMessage = validateAndPrepareEntity(excelData);

        if (CollectionUtils.isNotEmpty(errorMessage)) {
            return ExcelFileUtil.prepareErrorResponse(errorMessage);
        }
        return ExcelFileUtil.prepareSuccessResponse();
    }

    private List<String> validateAndPrepareEntity(ExcelData excelData) {
        List<String> errorMessages = new ArrayList<>();
        if (CollectionUtils.isEmpty(excelData.getDataList())) {
            return Collections.emptyList();
        }
        Set<String> positionNames = getAllNames();
        List<Position> positionList = new ArrayList<>();
        List<Map<String, ?>> dataList = excelData.getDataList();
        for (Map<String, ?> dataMap : dataList) {
            String positionName = (String) dataMap.get(NAME);
            if (positionNames.contains(positionName)) {
                errorMessages.add(String.format("Position: {%s} are already exists, Row: {%s}",
                        positionName, StringUtil.valueOf(dataMap.get(ApplicationConstant.ROW_NUMBER))));
            } else {
                Position position = new Position();
                position.setName(positionName);
                position.setDescription((String) dataMap.get(DESCRIPTION));
                positionList.add(position);
            }
        }
        if (CollectionUtils.isEmpty(errorMessages) && CollectionUtils.isNotEmpty(positionList)) {
            List<Position> positions = saveItemList(positionList);
            System.err.println(positions);
        }
        return errorMessages;
    }

    @Override
    public Set<String> getAllNames() {
        return positionRepository.getAllNames();
    }

    @Override
    public Set<Position> findAllActivePosition() {
        return positionRepository.findAllByIsActiveTrue();
    }

    public List<PositionProjection> findByIdIn(Set<Long> collectionsOfPositionIds) {
        return positionRepository.findAirportByIdIn(collectionsOfPositionIds);
    }

    @Override
    protected Specification<Position> buildSpecification(PositionSearchDto searchDto) {
        CustomSpecification<Position> customSpecification = new CustomSpecification<>();
        return Specification.where(customSpecification.equalSpecificationAtRoot(searchDto.getIsActive(), IS_ACTIVE_FIELD)
                .and(customSpecification.likeSpecificationAtRoot(searchDto.getName(), ApplicationConstant.ENTITY_NAME)));
    }
}
