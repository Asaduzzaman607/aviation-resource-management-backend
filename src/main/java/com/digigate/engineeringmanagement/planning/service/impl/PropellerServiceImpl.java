package com.digigate.engineeringmanagement.planning.service.impl;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.service.ReportService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Aircraft;
import com.digigate.engineeringmanagement.configurationmanagement.service.aircraftinformation.AircraftService;
import com.digigate.engineeringmanagement.planning.entity.Propeller;
import com.digigate.engineeringmanagement.planning.payload.request.MappingDto;
import com.digigate.engineeringmanagement.planning.payload.request.PropellerDto;
import com.digigate.engineeringmanagement.planning.payload.request.PropellerSearchDto;
import com.digigate.engineeringmanagement.planning.payload.response.PropellerViewModel;
import com.digigate.engineeringmanagement.planning.service.PropellerService;
import org.slf4j.MDC;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * Propeller Service
 *
 * @author Masud Rana
 */
@Service
public class PropellerServiceImpl
        extends AbstractSearchService<Propeller, PropellerDto, PropellerSearchDto> implements PropellerService {
    private final AircraftService aircraftService;
    private final ReportService reportService;
    private final AbstractRepository<Propeller> repository;
    private static final String NOMEN_CLATURE = "nomenClature";
    private static final String PART_NO = "partNo";
    private static final String AIRCRAFT = "aircraft";
    private static final String SERIAL_NO = "serialNo";
    private static final String ID = "id";
    private static final String ESTIMATED_DATE = "estimatedDate";
    private static final String IS_ACTIVE = "isActive";

    /**
     * Autowired constructor
     *  @param repository      {@link AbstractRepository}
     * @param aircraftService {@link AircraftService}
     * @param reportService
     */
    public PropellerServiceImpl(AbstractRepository<Propeller> repository,
                                AircraftService aircraftService, ReportService reportService) {
        super(repository);
        this.repository = repository;
        this.aircraftService = aircraftService;
        this.reportService = reportService;
    }

    /**
     * convert response  from entity
     *
     * @param propeller {@link Propeller}
     * @return {@link PropellerViewModel}
     */
    @Override
    protected PropellerViewModel convertToResponseDto(Propeller propeller) {
        return PropellerViewModel.builder()
                .id(propeller.getId())
                .nomenClature(propeller.getNomenClature())
                .partNo(propeller.getPartNo())
                .serialNo(propeller.getSerialNo())
                .installationDate(propeller.getInstallationDate())
                .installationTsn(propeller.getInstallationTsn())
                .installationTso(propeller.getInstallationTso())
                .currentTsn(propeller.getCurrentTsn())
                .currentTso(propeller.getCurrentTso())
                .limitMonth(propeller.getLimitMonth())
                .limitFh(propeller.getLimitFh())
                .estimatedDate(propeller.getEstimatedDate())
                .isActive(propeller.getIsActive())
                .build();
    }

    /**
     * convert entity  from dto
     *
     * @param propellerDto {@link PropellerDto}
     * @return {@link Propeller}
     */
    @Override
    protected Propeller convertToEntity(PropellerDto propellerDto) {
        if(Objects.isNull(propellerDto.getAircraftId())) {
            throw new EngineeringManagementServerException(
                    ErrorId.AIRCRAFT_ID_IS_REQUIRED,
                    HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID));
        }
        Propeller propeller =  prepareEntity(propellerDto, new Propeller());
        propeller.setIsActive(Boolean.TRUE);
        return propeller;
    }

    /**
     * convert entity  from dto
     *
     * @param propellerDto {@link PropellerDto}
     * @param propeller    {@link Propeller}
     * @return {@link Propeller}
     */
    @Override
    protected Propeller updateEntity(PropellerDto propellerDto, Propeller propeller) {
        return prepareEntity(propellerDto, propeller);
    }

    private Propeller prepareEntity(PropellerDto propellerDto, Propeller propeller) {
        if (Objects.nonNull(propellerDto.getAircraftId())) {
            Aircraft aircraft = aircraftService.findById(propellerDto.getAircraftId());
            propeller.setAircraft(aircraft);
        }
        propeller.setNomenClature(propellerDto.getNomenClature());
        propeller.setPartNo(propellerDto.getPartNo());
        propeller.setSerialNo(propellerDto.getSerialNo());
        propeller.setInstallationDate(propellerDto.getInstallationDate());
        propeller.setInstallationTsn(propellerDto.getInstallationTsn());
        propeller.setInstallationTso(propellerDto.getInstallationTso());
        propeller.setCurrentTsn(propellerDto.getCurrentTsn());
        propeller.setCurrentTso(propellerDto.getCurrentTso());
        propeller.setLimitFh(propellerDto.getLimitFh());
        propeller.setLimitFh(propellerDto.getLimitFh());
        propeller.setLimitMonth(propellerDto.getLimitMonth());
        propeller.setEstimatedDate(propellerDto.getEstimatedDate());
        return propeller;
    }

    /**
     * build specification for given entity
     *
     * @param searchDto {@link PropellerSearchDto}
     * @return {@link Specification<Propeller>}
     */
    @Override
    protected Specification<Propeller> buildSpecification(PropellerSearchDto searchDto) {
        CustomSpecification<Propeller> customSpecification = new CustomSpecification<>();
        Specification<Propeller> propellerSpecification =
                Specification.where(customSpecification.equalSpecificationAtRoot(searchDto.getPartNo(), PART_NO))
                        .and(customSpecification.likeSpecificationAtRoot(searchDto.getNomenClature(), NOMEN_CLATURE))
                        .and(customSpecification.equalSpecificationAtRoot(searchDto.getSerialNo(), SERIAL_NO))
                        .and(customSpecification.inSpecificationAtChild(searchDto.getAircraftIds(), AIRCRAFT, ID))
                        .and(customSpecification.inBetweenSpecification(
                                searchDto.getEstimatedStartDate(), searchDto.getEstimatedEndDate(), ESTIMATED_DATE))
                        .and(customSpecification.equalSpecificationAtRoot(searchDto.getIsActive(), IS_ACTIVE));
        return propellerSpecification;
    }

    /**
     * make relation between entity
     *
     * @param mappingDto {@link MappingDto}
     */
    @Override
    public void apply(MappingDto mappingDto) {
        Propeller propeller = findById(mappingDto.getId());
        Aircraft aircraft = aircraftService.findById(mappingDto.getAircraftId());
        propeller.setAircraft(aircraft);
        saveItem(propeller);
    }

    @Override
    public byte[] getReport(PropellerSearchDto searchDto, String fileType) {
        Specification<Propeller> propellerSpecification = buildSpecification(searchDto);
        List<Propeller> propellerList = repository.findAll(propellerSpecification);
        return reportService.prepareReport(propellerList, fileType, ApplicationConstant.PROPELLER_REPORT_PDF_FILE_NAME);
    }
}
