package com.digigate.engineeringmanagement.planning.service;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.configurationmanagement.service.aircraftinformation.AircraftService;
import com.digigate.engineeringmanagement.planning.entity.AmlBook;
import com.digigate.engineeringmanagement.planning.payload.request.AmlBookDto;
import com.digigate.engineeringmanagement.planning.payload.request.AmlBookSearchDto;
import com.digigate.engineeringmanagement.planning.payload.response.AmlBookViewModel;
import com.digigate.engineeringmanagement.planning.repository.AmlBookRepository;
import com.digigate.engineeringmanagement.planning.service.impl.IAmlBookService;
import org.slf4j.MDC;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

/**
 * AML Book Service implementation
 *
 * @author ashinisingha
 */
@Service
public class AmlBookService extends AbstractSearchService<AmlBook, AmlBookDto, AmlBookSearchDto>
        implements IAmlBookService {

    private final AmlBookRepository amlBookRepository;
    private final AircraftService aircraftService;
    private static final Integer TOTAL_PAGE = 50;
    private static final String AIRCRAFT_ID = "aircraftId";
    private static final String BOOK_NO = "bookNo";
    private static final String IS_ACTIVE = "isActive";

    /**
     * Autowired Constructor
     *
     * @param repository        {@link AbstractRepository<AmlBook>}
     * @param amlBookRepository {@link AmlBookRepository}
     * @param aircraftService   {@link AircraftService}
     */
    public AmlBookService(AbstractRepository<AmlBook> repository, AmlBookRepository amlBookRepository, AircraftService aircraftService) {
        super(repository);
        this.amlBookRepository = amlBookRepository;
        this.aircraftService = aircraftService;
    }

    /**
     * AML Book search Service
     *
     * @param searchDto {@link AmlBookSearchDto}
     * @return {@link Specification<AmlBook>}
     */
    @Override
    protected Specification<AmlBook> buildSpecification(AmlBookSearchDto searchDto) {
        CustomSpecification<AmlBook> customSpecification = new CustomSpecification<>();
        return Specification.where(
                customSpecification.equalSpecificationAtRoot(searchDto.getAircraftId(), AIRCRAFT_ID)
                        .and(customSpecification.likeSpecificationAtRoot(searchDto.getBookNo(), BOOK_NO))
                        .and(customSpecification.equalSpecificationAtRoot(searchDto.getIsActive(), IS_ACTIVE))
        );
    }

    /**
     * entity to view model converter method
     *
     * @param amlBook {@link AmlBook}
     * @return amlBookViewModel {@link AmlBookViewModel}
     */
    @Override
    protected AmlBookViewModel convertToResponseDto(AmlBook amlBook) {
        AmlBookViewModel amlBookViewModel = new AmlBookViewModel();
        amlBookViewModel.setId(amlBook.getId());
        amlBookViewModel.setAircraftId(amlBook.getAircraftId());
        amlBookViewModel.setAircraftName(amlBook.getAircraft().getAircraftName());
        amlBookViewModel.setBookNo(amlBook.getBookNo());
        amlBookViewModel.setStartPageNo(amlBook.getStartPageNo());
        amlBookViewModel.setEndPageNo(amlBook.getEndPageNo());
        amlBookViewModel.setIsActive(amlBook.getIsActive());
        return amlBookViewModel;
    }

    @Override
    protected AmlBook convertToEntity(AmlBookDto amlBookDto) {
        return saveOrUpdate(amlBookDto, new AmlBook());
    }

    @Override
    protected AmlBook updateEntity(AmlBookDto dto, AmlBook entity) {
        return saveOrUpdate(dto, entity);
    }

    /**
     * This method is responsible for validate client data
     *
     * @param amlBookDto {@link AmlBookDto}
     * @param id         {@link Long id}
     * @return {@link Boolean}
     */
    @Override
    public Boolean validateClientData(AmlBookDto amlBookDto, Long id) {
        AmlBook amlBook = amlBookRepository.findAmlBookByBookNo(amlBookDto.getBookNo());
        if (Objects.nonNull(amlBook) && !(Objects.nonNull(id) && amlBook.getId().equals(id))) {
            throw new EngineeringManagementServerException(
                    ErrorId.AML_BOOK_NO_IS_ALREADY_EXISTS,
                    HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID)
            );
        }

        if ((((Integer.parseInt(amlBookDto.getBookNo())) * TOTAL_PAGE) - 49) != amlBookDto.getStartPageNo()) {
            throw new EngineeringManagementServerException(
                    ErrorId.START_PAGE_IS_NOT_VALID,
                    HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID)
            );
        }
        if (((Integer.parseInt(amlBookDto.getBookNo())) * TOTAL_PAGE) != amlBookDto.getEndPageNo()) {
            throw new EngineeringManagementServerException(
                    ErrorId.END_PAGE_IS_NOT_VALID,
                    HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID)
            );
        }

        Optional<Long> book = amlBookRepository
                .findBYAircraftIdAndStartPage(amlBookDto.getAircraftId(), amlBookDto.getStartPageNo());

        if (book.isEmpty() || (Objects.nonNull(id) && book.get().equals(id))) {
            return Boolean.TRUE;
        } else {
            throw new EngineeringManagementServerException(
                    ErrorId.AML_BOOK_AIRCRAFT_ID_AND_START_PAGE_ALREADY_EXISTS,
                    HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID)
            );
        }
    }

    /**
     * this method is responsible for converting AML Book dto to entity for save/update purpose
     *
     * @param amlBookDto {@link AmlBookDto}
     * @param amlBook    {@link AmlBook}
     * @return amlbook {@link AmlBook}
     */
    private AmlBook saveOrUpdate(AmlBookDto amlBookDto, AmlBook amlBook) {
        amlBook.setAircraft(aircraftService.findById(amlBookDto.getAircraftId()));
        amlBook.setBookNo(amlBookDto.getBookNo());
        amlBook.setStartPageNo(amlBookDto.getStartPageNo());
        amlBook.setEndPageNo(amlBookDto.getEndPageNo());
        return amlBook;
    }

    /**
     * responsible for finding starting page of book using specific aircraft and page no
     *
     * @param aircraftId aircraft id
     * @param pageNumber page no
     * @return optional of start page no
     */
    @Override
    public Optional<Integer> findAmlBookByAircraftAndPageNumber(Long aircraftId, Integer pageNumber) {
        return amlBookRepository.findAmlBookByAircraftAndPageNumber(aircraftId, pageNumber);
    }
}
