package com.digigate.engineeringmanagement.planning.service;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.common.util.DateUtil;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Aircraft;
import com.digigate.engineeringmanagement.configurationmanagement.service.aircraftinformation.AircraftService;
import com.digigate.engineeringmanagement.planning.constant.PackageType;
import com.digigate.engineeringmanagement.planning.entity.*;
import com.digigate.engineeringmanagement.planning.payload.request.JobCardsDto;
import com.digigate.engineeringmanagement.planning.payload.request.WorkPackageDto;
import com.digigate.engineeringmanagement.planning.payload.request.WorkPackageSearchDto;
import com.digigate.engineeringmanagement.planning.payload.response.JobCardsViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.WorkPackageCertificateReportViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.WorkPackageSummaryReportViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.WorkPackageViewModel;
import com.digigate.engineeringmanagement.planning.repository.JobCardsRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Work Package Service
 *
 * @author ashinisingha
 */

@Service
public class WorkPackageService extends AbstractSearchService<WorkPackage, WorkPackageDto, WorkPackageSearchDto>
        implements WorkPackageIService {

    private static final String PLUS_DELIMITER = "+";
    private static final String CATEGORY_B1 = "B1";
    private static final String CATEGORY_B2 = "B2";
    private static final String AIRCRAFT_ID = "aircraftId";
    private static final String IS_ACTIVE = "isActive";
    private static final String PACKAGE_TYPE = "packageType";

    private final AircraftService aircraftService;
    private final AircraftCheckIndexService aircraftCheckIndexService;
    private final JobCardsRepository jobCardsRepository;


    /**
     * autowired constructor
     *
     * @param repository                {@link AbstractRepository<WorkPackage>}
     * @param aircraftService           {@link AircraftService}
     * @param aircraftCheckIndexService {@link AircraftCheckIndex}
     * @param jobCardsRepository
     */
    @Autowired
    public WorkPackageService(AbstractRepository<WorkPackage> repository, AircraftService aircraftService,
                              AircraftCheckIndexService aircraftCheckIndexService, JobCardsRepository jobCardsRepository) {
        super(repository);
        this.aircraftService = aircraftService;
        this.aircraftCheckIndexService = aircraftCheckIndexService;
        this.jobCardsRepository = jobCardsRepository;
    }

    @Override
    protected Specification<WorkPackage> buildSpecification(WorkPackageSearchDto searchDto) {
        CustomSpecification<WorkPackage> customSpecification = new CustomSpecification<>();
        return Specification.where(
                customSpecification.equalSpecificationAtRoot(searchDto.getAircraftId(), AIRCRAFT_ID)
                        .and(customSpecification.equalSpecificationAtRoot(searchDto.getPackageType(), PACKAGE_TYPE))
                        .and(customSpecification.equalSpecificationAtRoot(searchDto.getIsActive(), IS_ACTIVE))
        );
    }

    @Override
    protected WorkPackageViewModel convertToResponseDto(WorkPackage workPackage) {
        WorkPackageViewModel workPackageViewModel = new WorkPackageViewModel();
        workPackageViewModel.setWorkPackageId(workPackage.getId());
        workPackageViewModel.setAircraftId(workPackage.getAircraftId());
        workPackageViewModel.setAircraftName(workPackage.getAircraft().getAircraftName());

        workPackageViewModel.setAcCheckIndexId(workPackage.getAcCheckIndexId());
        workPackageViewModel.setCheckName(prepareAircraftChecks(workPackage.getAircraftCheckIndex()));

        workPackageViewModel.setPackageType(workPackage.getPackageType());
        workPackageViewModel.setInputDate(workPackage.getInputDate());
        workPackageViewModel.setReleaseDate(workPackage.getReleaseDate());
        workPackageViewModel.setAcHours(workPackage.getAcHours());
        workPackageViewModel.setAcCycle(workPackage.getAcCycle());
        workPackageViewModel.setIsActive(workPackage.getIsActive());
        workPackageViewModel.setAsOfDate(workPackage.getAsOfDate());
        List<JobCardsViewModel> jobCardsViewModels = new ArrayList<>();
        if (workPackage.getPackageType().equals(PackageType.WORK_PACKAGE_CERTIFICATE_RECORD)) {
            workPackage.getJobCardsList().forEach(jobCards -> {
                jobCardsViewModels.add(convertToJobCardResponse(jobCards));
            });
            workPackageViewModel.setJobCardsViewModels(jobCardsViewModels);
        }
        return workPackageViewModel;
    }

    @Override
    protected WorkPackage convertToEntity(WorkPackageDto workPackageDto) {
        return saveOrUpdate(workPackageDto, new WorkPackage());
    }

    @Override
    protected WorkPackage updateEntity(WorkPackageDto dto, WorkPackage entity) {
        return saveOrUpdate(dto, entity);
    }

    private WorkPackage saveOrUpdate(WorkPackageDto workPackageDto, WorkPackage workPackage) {
        Aircraft aircraft = aircraftService.findById(workPackageDto.getAircraftId());
        AircraftCheckIndex aircraftCheckIndex = aircraftCheckIndexService
                .findById(workPackageDto.getAcCheckIndexId());

        workPackage.setAircraft(aircraft);
        workPackage.setAircraftCheckIndex(aircraftCheckIndex);
        if (Objects.isNull(workPackageDto.getPackageType())) {
            workPackage.setPackageType(PackageType.WORK_PACKAGE_SUMMARY);
        } else {
            workPackage.setPackageType(workPackageDto.getPackageType());
        }
        workPackage.setInputDate(workPackageDto.getInputDate());
        workPackage.setReleaseDate(workPackageDto.getReleaseDate());
        workPackage.setAcHours(workPackageDto.getAcHours());
        workPackage.setAcCycle(workPackageDto.getAcCycle());
        workPackage.setAsOfDate(workPackageDto.getAsOfDate());
        return workPackage;
    }

    public JobCardsViewModel convertToJobCardResponse(JobCards jobCards) {
        return JobCardsViewModel.builder()
                .jobCardsId(jobCards.getId())
                .jobCategory(jobCards.getJobCategory())
                .completed(jobCards.getCompleted())
                .total(jobCards.getTotal())
                .deferred(jobCards.getDeferred())
                .remark(jobCards.getRemark())
                .withDrawn(jobCards.getWithDrawn())
                .build();
    }

    @Override
    @Transactional
    public WorkPackage saveCertification(WorkPackageDto dto) {
        dto.setPackageType(PackageType.WORK_PACKAGE_CERTIFICATE_RECORD);
        WorkPackage workPackage = saveItem(convertToEntity(dto));
        List<JobCards> jobCards = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(dto.getJobCardsDtoList())) {
            dto.getJobCardsDtoList().forEach(job -> {
                jobCards.add(mapToJobCardsEntity(job, workPackage));
            });
            jobCardsRepository.saveAll(jobCards);
        }

        return workPackage;
    }

    @Override
    @Transactional
    public WorkPackage updateCertification(WorkPackageDto dto, Long id) {
        dto.setPackageType(PackageType.WORK_PACKAGE_CERTIFICATE_RECORD);
        WorkPackage workPackage = saveItem(updateEntity(dto, findByIdUnfiltered(id)));
        List<JobCards> jobCards = new ArrayList<>();
        dto.getJobCardsDtoList().forEach(job -> {
            jobCards.add(mapToJobCardsEntity(job, workPackage));
        });

        if (CollectionUtils.isNotEmpty(workPackage.getJobCardsList())) {
            jobCardsRepository.deleteAll(workPackage.getJobCardsList());
        }

        if (CollectionUtils.isNotEmpty(jobCards)) {
            jobCardsRepository.saveAll(jobCards);
        }
        return workPackage;
    }

    public JobCards mapToJobCardsEntity(JobCardsDto dto, WorkPackage workPackage) {
        JobCards jobCards = new JobCards();
        jobCards.setWorkPackage(workPackage);
        jobCards.setTotal(dto.getTotal());
        jobCards.setJobCategory(dto.getJobCategory());
        jobCards.setCompleted(dto.getCompleted());
        jobCards.setWithDrawn(dto.getWithDrawn());
        jobCards.setDeferred(dto.getDeferred());
        jobCards.setRemark(dto.getRemark());
        return jobCards;
    }

    private String prepareAircraftChecks(AircraftCheckIndex aircraftCheckIndex) {
        List<String> acCheckIndexNameList = new ArrayList<>();
        Set<AircraftCheck> aircraftCheckSet = aircraftCheckIndex.getAircraftTypeCheckSet();
        aircraftCheckSet.forEach(
                aircraftCheck -> {
                    String acCheckIndexName = Objects.nonNull(aircraftCheck.getCheck()) ? aircraftCheck.getCheck()
                            .getTitle() : null;
                    if (Objects.nonNull(acCheckIndexName)) {
                        acCheckIndexNameList.add(acCheckIndexName);
                    }
                }
        );
        List<String> sortedAcCheckIndexNameList = acCheckIndexNameList.stream().sorted().collect(Collectors.toList());
        return String.join(PLUS_DELIMITER, sortedAcCheckIndexNameList);
    }


    @Override
    public WorkPackageSummaryReportViewModel getReport(Long workPackageId) {

        WorkPackage workPackage = findById(workPackageId);
        AircraftCheckIndex aircraftCheckIndex = workPackage.getAircraftCheckIndex();
        WorkPackageSummaryReportViewModel workPackageSummaryReportViewModel = new WorkPackageSummaryReportViewModel();

        Integer totalCards = 0;
        Integer totalB1Category = 0;
        Integer totalB2Category = 0;
        if (Objects.nonNull(aircraftCheckIndex.getLdndSet())) {
            totalCards = aircraftCheckIndex.getLdndSet().size();
            for (Ldnd ldnd : aircraftCheckIndex.getLdndSet()) {
                for (String category : ldnd.getTask().getTrade()) {
                    if (category.equals(CATEGORY_B1)) {
                        totalB1Category++;
                    }
                    if (category.equals(CATEGORY_B2)) {
                        totalB2Category++;
                    }
                }
            }
        }

        Double flyHours = null;
        if (Objects.nonNull(aircraftCheckIndex.getAircraftTypeCheckSet())) {
            for (AircraftCheck aircraftCheck : aircraftCheckIndex.getAircraftTypeCheckSet()) {
                if (Objects.nonNull(aircraftCheck.getFlyingHour())) {
                    if (Objects.isNull(flyHours)) {
                        flyHours = aircraftCheck.getFlyingHour();
                    } else {
                        flyHours = Math.min(flyHours, aircraftCheck.getFlyingHour());
                    }
                }
            }
        }

        if (Objects.nonNull(aircraftCheckIndex.getDoneHour()) && Objects.nonNull(flyHours)) {
            workPackageSummaryReportViewModel.setDueAt(DateUtil.addTimes(aircraftCheckIndex.getDoneHour(), flyHours));
        }

        workPackageSummaryReportViewModel.setAsOn(workPackage.getReleaseDate());
        workPackageSummaryReportViewModel.setInputDate(workPackage.getInputDate());
        workPackageSummaryReportViewModel.setAircraftName(aircraftCheckIndex.getAircraft().getAircraftName());
        workPackageSummaryReportViewModel.setAircraftModelName(aircraftCheckIndex.getAircraft()
                .getAircraftModel().getAircraftModelName());
        workPackageSummaryReportViewModel.setAcHours(workPackage.getAcHours());
        workPackageSummaryReportViewModel.setAcCycles(workPackage.getAcCycle());

        if (Objects.nonNull(aircraftCheckIndex.getWorkOrder())) {
            workPackageSummaryReportViewModel.setWoNo(aircraftCheckIndex.getWorkOrder().getWoNo());
            workPackageSummaryReportViewModel.setWoDate(aircraftCheckIndex.getWorkOrder().getDate());
        }

        workPackageSummaryReportViewModel.setCheckNo(prepareAircraftChecks(aircraftCheckIndex));
        workPackageSummaryReportViewModel.setNoOfTaskCards(totalCards);
        workPackageSummaryReportViewModel.setCategoryB1(totalB1Category);
        workPackageSummaryReportViewModel.setCategoryB2(totalB2Category);
        workPackageSummaryReportViewModel.setAsOfDate(workPackage.getAsOfDate());
        return workPackageSummaryReportViewModel;
    }

    @Override
    public WorkPackageCertificateReportViewModel getCertificateReport(Long workPackageId) {
        WorkPackageCertificateReportViewModel reportViewModel = new WorkPackageCertificateReportViewModel();
        WorkPackage workPackage = findById(workPackageId);
        AircraftCheckIndex aircraftCheckIndex = workPackage.getAircraftCheckIndex();

        reportViewModel.setAsOn(workPackage.getReleaseDate());
        reportViewModel.setInputDate(workPackage.getInputDate());
        reportViewModel.setAircraftName(aircraftCheckIndex.getAircraft().getAircraftName());
        reportViewModel.setAircraftModelName(aircraftCheckIndex.getAircraft()
                .getAircraftModel().getAircraftModelName());
        reportViewModel.setAcHours(workPackage.getAcHours());
        reportViewModel.setAcCycles(workPackage.getAcCycle());
        reportViewModel.setCheckNo(prepareAircraftChecks(aircraftCheckIndex));
        reportViewModel.setAsOfDate(workPackage.getAsOfDate());

        if (Objects.nonNull(aircraftCheckIndex.getWorkOrder())) {
            reportViewModel.setWoNo(aircraftCheckIndex.getWorkOrder().getWoNo());
            reportViewModel.setWoDate(aircraftCheckIndex.getWorkOrder().getDate());
        }
        List<JobCardsViewModel> jobCardsViewModels = new ArrayList<>();
        if (workPackage.getPackageType().equals(PackageType.WORK_PACKAGE_CERTIFICATE_RECORD)) {
            workPackage.getJobCardsList().forEach(jobCards -> {
                jobCardsViewModels.add(convertToJobCardResponse(jobCards));
            });
            reportViewModel.setJobCardsViewModels(jobCardsViewModels);
        }
        return reportViewModel;
    }
}
