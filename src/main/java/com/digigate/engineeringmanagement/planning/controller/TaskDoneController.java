package com.digigate.engineeringmanagement.planning.controller;

import com.digigate.engineeringmanagement.common.config.model.ExcelDataResponse;
import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.controller.AbstractController;
import com.digigate.engineeringmanagement.common.payload.response.MessageResponse;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.service.ISearchService;
import com.digigate.engineeringmanagement.common.service.IService;
import com.digigate.engineeringmanagement.planning.entity.TaskDone;
import com.digigate.engineeringmanagement.planning.payload.request.TaskDoneDto;
import com.digigate.engineeringmanagement.planning.payload.request.TaskDoneSaveDto;
import com.digigate.engineeringmanagement.planning.payload.request.TaskDoneSearchDto;
import com.digigate.engineeringmanagement.planning.payload.response.*;
import com.digigate.engineeringmanagement.planning.service.LdndService;
import com.digigate.engineeringmanagement.planning.service.TaskDoneIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

/**
 * Task Done Controller
 *
 * @author Asifur Rahman
 */
@RestController
@RequestMapping("/api/task-done")
public class TaskDoneController extends AbstractController<TaskDone, TaskDoneDto> {

    private final TaskDoneIService taskDoneIService;
    private final LdndService ldndService;

    private static final String UPDATE_LDND_DATA_SUCCESSFULLY = "Update Ldnd Data Successfully";

    /**
     * Parameterized constructor
     *
     * @param taskDoneService  {@link ISearchService}
     * @param taskDoneIService {@link TaskDoneIService}
     * @param ldndService
     */
    @Autowired
    public TaskDoneController(IService<TaskDone, TaskDoneDto> taskDoneService,
                              TaskDoneIService taskDoneIService, LdndService ldndService) {
        super(taskDoneService);
        this.taskDoneIService = taskDoneIService;
        this.ldndService = ldndService;
    }

    /**
     * This Method will provide Task and Position by Aircraft
     *
     * @param aircraftId {@link Long}
     * @return {@link  List<TaskDoneSaveDto>}
     */
    @GetMapping("/task-by-aircraft/{aircraftId}")
    public List<TaskDoneSaveDto> getTaskAndPositionByAircraftId(@PathVariable Long aircraftId) {
        return taskDoneIService.getTaskAndPositionByAircraftId(aircraftId);
    }

    /**
     * get task position by taskId
     *
     * @param taskId {@link Long}
     * @return {@link  List<TaskDoneSaveDto>}
     */
    @GetMapping("/positions/{taskId}")
    public ResponseEntity<List<TaskDonePositionDto>> getTaskPositionByTaskId(@PathVariable Long taskId) {
        return new ResponseEntity<>(taskDoneIService.getTaskPositionByTaskId(taskId), HttpStatus.OK);
    }

    /**
     * get task position by taskId
     *
     * @param findDto {@link LdndDataFindDto}
     * @return        LdndData as ViewModel
     */
    @PostMapping("/find-by-aircrafts")
    public ResponseEntity<List<LdndDataViewModel>> getLdndListByAircraftAndDueDate(
            @Valid @RequestBody LdndDataFindDto findDto) {
        return new ResponseEntity<>(taskDoneIService.getLdndListByAircraftAndDueDate(findDto), HttpStatus.OK);
    }

    /**
     * search api of task done
     *
     * @return PageData {@link PageData}
     */
    @PostMapping("/search")
    public PageData searchTaskDone(@Valid @RequestBody TaskDoneSearchDto searchDto,
                                   @PageableDefault(sort = ApplicationConstant.DEFAULT_SORT,
                                           direction = Sort.Direction.ASC) Pageable pageable) {
        return taskDoneIService.searchTaskDone(searchDto, pageable);
    }


    /**
     * get part-serial-list
     *
     * @param modelId {@link Long}
     * @param aircraftId {@link Long}
     * @return {@link  Set<AcPartSerialResponse>}
     */
    @GetMapping("/part-serial-list")
    public ResponseEntity<Set<AcPartSerialResponse>> findPartSerialList(
            @RequestParam(value = "aircraft_id") Long aircraftId,
            @RequestParam(value = "model_id") Long modelId) {
        return new ResponseEntity<>(taskDoneIService.findAcPartSerialResponse(aircraftId, modelId), HttpStatus.OK);
    }

    @PostMapping("/ldnd")
    public ResponseEntity<LdndViewModel> getCalculatedLdnd(@Valid @RequestBody TaskDoneDto taskDoneDto) {
        return new ResponseEntity<>(ldndService.getCalculatedLdnd(taskDoneDto), HttpStatus.OK);
    }


    @PostMapping("/upload/{aircraftId}")
    public ResponseEntity<ExcelDataResponse> importExcelFile(
            @RequestParam("file") MultipartFile file, @PathVariable Long aircraftId) {
        ExcelDataResponse excelDataResponse = taskDoneIService.uploadExcel(file, aircraftId);
        return new ResponseEntity<>(excelDataResponse, excelDataResponse.getStatus());
    }

    @GetMapping("/update_ldnd")
    public ResponseEntity<MessageResponse> updateLdndRemainingValueCalculation(){
        ldndService.processAndUpdateLdndRemainingValueCalculation();
        return ResponseEntity.ok(new MessageResponse(UPDATE_LDND_DATA_SUCCESSFULLY));
    }
}
