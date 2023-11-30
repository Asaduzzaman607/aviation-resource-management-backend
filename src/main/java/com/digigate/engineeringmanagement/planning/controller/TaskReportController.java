package com.digigate.engineeringmanagement.planning.controller;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.planning.payload.request.*;
import com.digigate.engineeringmanagement.planning.payload.response.AdReportTitleDataViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.TaskReportViewModel;
import com.digigate.engineeringmanagement.planning.service.TaskReportService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * TaskReportController Controller
 *
 * @author Asifur Rahman
 */
@RestController
@RequestMapping("/api/task-report")
public class TaskReportController {

    private final TaskReportService taskReportService;

    /**
     * Parameterized constructor
     */
    public TaskReportController(TaskReportService taskReportService) {
        this.taskReportService = taskReportService;
    }

    /**
     * report data generation api
     *
     * @return PageData {@link List<TaskReportViewModel>}
     */
    @PostMapping("/ldnd")
    public PageData getLdNdReportData(@Valid @RequestBody LdndReportSearchDto searchDto,
                                      @PageableDefault(
                                              sort = ApplicationConstant.DEFAULT_SORT,
                                              direction = Sort.Direction.ASC) Pageable pageable) {
        return taskReportService.getLdNdReportData(searchDto, pageable);
    }

    @PostMapping("/hard-time-report")
    public PageData getLdndHardTimeReport(@Valid @RequestBody LdndReportSearchDto searchDto,
                                          @PageableDefault(
                                                  sort = ApplicationConstant.DEFAULT_SORT,
                                                  direction = Sort.Direction.ASC) Pageable pageable) {
        return taskReportService.getLdndHardTimeReport(searchDto, pageable);
    }


    /**
     * update ldnd data
     *
     * @return {@link List<Long>}
     */
    @GetMapping("/update-ldnd-data")
    public List<Long> updateLdndData() {
        return taskReportService.updateLdndData();
    }

    /**
     * AD report generation api
     *
     * @param adReportSearchDto {@link AdReportSearchDto}
     * @return pageData {@link PageData}
     */
    @PostMapping("/ad-report")
    public PageData getAdReportData(@Valid @RequestBody AdReportSearchDto adReportSearchDto, @PageableDefault(
            sort = ApplicationConstant.DEFAULT_SORT,
            direction = Sort.Direction.ASC) Pageable pageable) {
        return taskReportService.getAdReportData(adReportSearchDto, pageable);
    }

    @PostMapping("/sb-report")
    public PageData getSbReportData(@Valid @RequestBody AdReportSearchDto adReportSearchDto, @PageableDefault(
            sort = ApplicationConstant.DEFAULT_SORT,
            direction = Sort.Direction.ASC) Pageable pageable) {
        return taskReportService.getSbReport(adReportSearchDto, pageable);
    }

    @PostMapping("/stc-report")
    public PageData getStcReportData(@Valid @RequestBody AdReportSearchDto adReportSearchDto, @PageableDefault(
            sort = ApplicationConstant.DEFAULT_SORT,
            direction = Sort.Direction.ASC) Pageable pageable) {
        return taskReportService.getStcReport(adReportSearchDto, pageable);
    }


    @PostMapping("/engine-ad-report")
    public PageData getAdReportData(@Valid @RequestBody EngineAdReportSearchDto engineAdReportSearchDto,
                                    @PageableDefault(sort = ApplicationConstant.DEFAULT_SORT,
                                            direction = Sort.Direction.ASC) Pageable pageable) {
        return taskReportService.getAdEngineReportData(engineAdReportSearchDto, pageable);
    }

    /**
     * AD report title generation api
     *
     * @param aircraftId {@link Long}
     * @return adReportTitleDataViewModel {@link AdReportTitleDataViewModel}
     */
    @GetMapping("/ad-report-title/{aircraftId}")
    public AdReportTitleDataViewModel getAdReportTitleData(@PathVariable Long aircraftId) {
        return taskReportService.getAdReportTitleData(aircraftId);
    }

    @PostMapping("/task-status-report")
    public PageData getTaskStatusReport(@Valid @RequestBody TaskStatusReportSearchDto taskStatusReportSearchDto,
                                        @PageableDefault(sort = ApplicationConstant.DEFAULT_SORT,
                                                direction = Sort.Direction.ASC) Pageable pageable) {
        return taskReportService.getTaskStatusReport(taskStatusReportSearchDto, pageable);
    }

    @PostMapping("/search-task-list-by-source")
    public PageData searchTaskListByTaskSourceType(@Valid @RequestBody TaskListSearchDto taskListSearchDto,
                                                   @PageableDefault(sort = ApplicationConstant.DEFAULT_SORT,
                                                           direction = Sort.Direction.ASC) Pageable pageable) {
        return taskReportService.searchTaskListByTaskSourceType(taskListSearchDto, pageable);
    }


}
