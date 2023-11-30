package com.digigate.engineeringmanagement.planning.service;

import com.digigate.engineeringmanagement.common.config.model.ExcelData;
import com.digigate.engineeringmanagement.common.config.model.ExcelDataResponse;
import com.digigate.engineeringmanagement.common.config.util.ExcelFileUtil;
import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.common.util.StringUtil;
import com.digigate.engineeringmanagement.planning.entity.AircraftLocation;
import com.digigate.engineeringmanagement.planning.payload.request.AircraftLocationDto;
import com.digigate.engineeringmanagement.planning.payload.request.AircraftSearchLocationDto;
import com.digigate.engineeringmanagement.planning.repository.AircraftLocationRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.MDC;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.IS_ACTIVE_FIELD;

/**
 * Airport Location implementation
 *
 * @author ashiniSingha
 */
@Service
public class AircraftLocationService extends AbstractSearchService<AircraftLocation, AircraftLocationDto, AircraftSearchLocationDto> implements
AircraftLocationIService{
    private final AircraftLocationRepository aircraftLocationRepository;
    private final Environment environment;

    private static final String LOCATION = "Location";
    private static final String LOCATION_COLUMN_NAME = "Location";
    private static final String DESCRIPTION = "Description";
    private static final String REMARKS = "Remarks";
    private static final String ARM_EXCEL_LOCATION= "arm.excel.upload.file.name.location";

    /**
     * Autowired constructor
     *
     * @param aircraftLocationRepository {@link AircraftLocationRepository}
     * @param environment                {@link Environment}
     */
    public AircraftLocationService(AircraftLocationRepository aircraftLocationRepository, Environment environment) {
        super(aircraftLocationRepository);
        this.aircraftLocationRepository = aircraftLocationRepository;
        this.environment = environment;
    }

    /**
     * This method is responsible for creating an Aircraft Location
     *
     * @param aircraftLocationDto           {@link AircraftLocationDto}
     * @return newly created entity         {@link AircraftLocation}
     */
    @Override
    public AircraftLocation create(AircraftLocationDto aircraftLocationDto) {

        if(aircraftLocationRepository.existsByName(aircraftLocationDto.getName())){
            throw new EngineeringManagementServerException(
                    ErrorId.AIRPORT_LOCATION_NAME_ALREADY_EXISTS,
                    HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID)
            );
        }
        aircraftLocationDto.setIsActive(Boolean.TRUE);
        return super.create(aircraftLocationDto);
    }



    @Override
    protected AircraftLocationDto convertToResponseDto(AircraftLocation aircraftLocation) {

        return AircraftLocationDto.builder()
                .id(aircraftLocation.getId())
                .name(aircraftLocation.getName())
                .description(aircraftLocation.getDescription())
                .remarks(aircraftLocation.getRemarks())
                .isActive(aircraftLocation.getIsActive())
                .build();

    }

    @Override
    protected AircraftLocation convertToEntity(AircraftLocationDto aircraftLocationDto) {

        AircraftLocation aircraftLocation = new AircraftLocation();

        aircraftLocation.setName(aircraftLocationDto.getName());
        aircraftLocation.setDescription(aircraftLocationDto.getDescription());
        aircraftLocation.setRemarks(aircraftLocationDto.getRemarks());
        aircraftLocation.setIsActive(aircraftLocationDto.getIsActive());
        return aircraftLocation;
    }

    @Override
    protected AircraftLocation updateEntity(AircraftLocationDto dto, AircraftLocation entity) {
        if (!dto.getName().equals(entity.getName())){
            if(aircraftLocationRepository.existsByName(dto.getName())){
                throw new EngineeringManagementServerException(
                        ErrorId.AIRPORT_LOCATION_NAME_ALREADY_EXISTS,
                        HttpStatus.BAD_REQUEST,
                        MDC.get(ApplicationConstant.TRACE_ID)
                );
            }
        }


        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setRemarks(dto.getRemarks());


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
                .getExcelDataFromSheet(file, environment.getProperty(ARM_EXCEL_LOCATION), LOCATION);

        if (CollectionUtils.isNotEmpty(excelData.getErrorMessages())) {
            return ExcelFileUtil.prepareErrorResponse(excelData.getErrorMessages());
        }
        List<String> errorMessage = validateAndSaveEntity(excelData);

        if (CollectionUtils.isNotEmpty(errorMessage)) {
            return ExcelFileUtil.prepareErrorResponse(errorMessage);
        }
        return ExcelFileUtil.prepareSuccessResponse();
    }

    @Override
    public Set<AircraftLocation> findAllActiveAircraftLocation() {
        return aircraftLocationRepository.findAllByIsActiveTrue();
    }

    private List<String> validateAndSaveEntity(ExcelData excelData) {
        List<String> errorMessages = new ArrayList<>();
        if (CollectionUtils.isEmpty(excelData.getDataList())) {
            return Collections.emptyList();
        }
        Set<String> names = aircraftLocationRepository.getAllNames();
        List<Map<String, ?>> dataList = excelData.getDataList();
        List<AircraftLocation> locationList = new ArrayList<>();

        for (Map<String, ?> dataMap : dataList) {
            String locationName = (String) dataMap.get(LOCATION_COLUMN_NAME);
            if (names.contains(locationName)) {
                errorMessages.add(String.format("Aircraft Location: {%s} are already exists, Row: {%s}",
                        locationName, StringUtil.valueOf(dataMap.get(ApplicationConstant.ROW_NUMBER))));
            } else {
                AircraftLocation location = new AircraftLocation();
                location.setName(locationName);
                location.setDescription((String) dataMap.get(DESCRIPTION));
                location.setRemarks((String) dataMap.get(REMARKS));
                locationList.add(location);
            }
        }
        if (CollectionUtils.isEmpty(errorMessages) && CollectionUtils.isNotEmpty(locationList)) {
            saveItemList(locationList);
        }
        return errorMessages;
    }

    @Override
    protected Specification<AircraftLocation> buildSpecification(AircraftSearchLocationDto searchDto) {
        CustomSpecification<AircraftLocation> customSpecification = new CustomSpecification<>();
        return Specification.where(customSpecification.equalSpecificationAtRoot(searchDto.getIsActive(), IS_ACTIVE_FIELD)
                .and(customSpecification.likeSpecificationAtRoot(searchDto.getName(), ApplicationConstant.ENTITY_NAME)));
    }
}
