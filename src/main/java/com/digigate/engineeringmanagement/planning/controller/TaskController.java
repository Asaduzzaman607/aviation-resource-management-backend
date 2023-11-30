package com.digigate.engineeringmanagement.planning.controller;

import com.digigate.engineeringmanagement.common.config.model.ExcelDataResponse;
import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.common.service.ISearchService;
import com.digigate.engineeringmanagement.planning.entity.Task;
import com.digigate.engineeringmanagement.planning.payload.request.AircraftEffectivityTaskDto;
import com.digigate.engineeringmanagement.planning.payload.request.TaskDto;
import com.digigate.engineeringmanagement.planning.payload.request.TaskSearchDto;
import com.digigate.engineeringmanagement.planning.payload.response.TaskModelResponseDto;
import com.digigate.engineeringmanagement.planning.payload.response.TaskViewModel;
import com.digigate.engineeringmanagement.planning.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Task Controller
 *
 * @author ashinisingha
 */
@RestController
@RequestMapping("/api/task")
public class TaskController extends AbstractSearchController<Task, TaskDto, TaskSearchDto> {

    private final TaskService taskService;

    /**
     * Parameterized constructor
     *
     * @param iSearchService {@link ISearchService}
     * @param taskService    {@link TaskService}
     */
    public TaskController(ISearchService<Task, TaskDto, TaskSearchDto> iSearchService,
                          TaskService taskService) {
        super(iSearchService);
        this.taskService = taskService;
    }

    /**
     * Get Api by aircraft model id
     *
     * @param aircraftModelId {@link Long}
     * @return {@link List<TaskViewModel>}
     */
    @GetMapping("/all/{aircraftModelId}")
    public ResponseEntity<List<TaskViewModel>>
    getModelListByAircraftModelId(@PathVariable Long aircraftModelId) {
        return ResponseEntity.ok(taskService.findTaskByAircraftModelId(aircraftModelId));
    }

    /**
     * This method will find all method of aircraft model by aircraft
     * @param aircraftId           {@link Long}
     * @return                     {@link List<TaskViewModel>}
     */
    @GetMapping("/aircraft/{aircraftId}")
    public List<AircraftEffectivityTaskDto> getTaskListByAircraft(@PathVariable Long aircraftId){
        return taskService.getTaskListByAircraft(aircraftId);
    }

    /**
     * This method will save tasks for specific aircraft
     *
     * @param aircraftEffectivityTaskDtoList {@link List}
     */
    @PostMapping("/specific-task")
    public void saveAircraftSpecificTask(
            @RequestBody List<AircraftEffectivityTaskDto> aircraftEffectivityTaskDtoList) {
        taskService.saveAircraftSpecificTask(aircraftEffectivityTaskDtoList);
    }
    @GetMapping("/task-by-aircraft-model")
    public List<TaskModelResponseDto> getTaskListByAircraftModelId(@RequestParam("aircraftModelId") Long aircraftModelId) {
        return taskService.getTaskModelByAircraftModelId(aircraftModelId);
    }

    /**
     * Upload excel for task
     *
     * @param file            {@link  MultipartFile}
     * @param aircraftModelId {@link  Long}
     * @return                {@link ResponseEntity}
     */
    @PostMapping("/upload/{aircraftModelId}")
    public ResponseEntity<ExcelDataResponse> importTaskExcelFile(
            @RequestParam("file") MultipartFile file, @PathVariable Long aircraftModelId) {
        ExcelDataResponse excelDataResponse = taskService.uploadTaskExcel(file, aircraftModelId);
        return new ResponseEntity<>(excelDataResponse, excelDataResponse.getStatus());
    }

    @PostMapping("/upload-aircraft-effectivity/{aircraftModelId}")
    public ResponseEntity<ExcelDataResponse> importExcelFileAircraftEffectivity(
            @RequestParam("file") MultipartFile file, @PathVariable Long aircraftModelId) {
        ExcelDataResponse excelDataResponse =
                taskService.importExcelFileAircraftEffectivity(file, aircraftModelId);
        return new ResponseEntity<>(excelDataResponse, excelDataResponse.getStatus());
    }

    @PostMapping("/upload-task-procedure/{aircraftModelId}")
    public ResponseEntity<ExcelDataResponse> importExcelFileTaskProcedure(
            @RequestParam("file") MultipartFile file, @PathVariable Long aircraftModelId) {
        ExcelDataResponse excelDataResponse = taskService.importExcelFileTaskProcedure(file, aircraftModelId);
        return new ResponseEntity<>(excelDataResponse, excelDataResponse.getStatus());
    }

    @PostMapping("/upload-consumable-part/{aircraftModelId}")
    public ResponseEntity<ExcelDataResponse> importExcelFileConsumableParts(
            @RequestParam("file") MultipartFile file, @PathVariable Long aircraftModelId) {
        ExcelDataResponse excelDataResponse = taskService.importExcelFileConsumableParts(file, aircraftModelId);
        return new ResponseEntity<>(excelDataResponse, excelDataResponse.getStatus());
    }
}
