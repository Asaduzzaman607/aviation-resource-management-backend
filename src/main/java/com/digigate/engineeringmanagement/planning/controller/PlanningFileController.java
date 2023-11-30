package com.digigate.engineeringmanagement.planning.controller;

import com.digigate.engineeringmanagement.common.controller.AbstractController;
import com.digigate.engineeringmanagement.common.payload.response.MessageResponse;
import com.digigate.engineeringmanagement.common.service.IService;
import com.digigate.engineeringmanagement.planning.entity.PlanningFile;
import com.digigate.engineeringmanagement.planning.payload.request.FileSearchDto;
import com.digigate.engineeringmanagement.planning.payload.request.PlanningFileDto;
import com.digigate.engineeringmanagement.planning.payload.request.ValidateMatchStringDto;
import com.digigate.engineeringmanagement.planning.payload.response.PlanningFileViewModel;
import com.digigate.engineeringmanagement.planning.service.PlanningFileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * PlanningFile Controller
 *
 * @author Junaid Khan Pathan
 */

@RestController
@RequestMapping("/api/planning-files")
public class PlanningFileController extends AbstractController<PlanningFile, PlanningFileDto> {

    private static final String CREATED_SUCCESSFULLY_MESSAGE = "Created Successfully";

    private static final String NO_DUPLICATE_MATCH_STRING = "no duplicate match string";
    private final PlanningFileService planningFileService;

    /**
     * Parameterized Constructor
     *
     * @param iService                  {@link IService}
     * @param planningFileService       {@link PlanningFileService}
     */
    public PlanningFileController(IService<PlanningFile, PlanningFileDto> iService,
                                  PlanningFileService planningFileService) {
        super(iService);
        this.planningFileService = planningFileService;
    }

    /**
     * This endpoint takes a folder id and returns a list of all planning files inside this folder.
     *
     * @param id        {@link Long}
     * @return          returns a list of all planning files inside a folder
     */
    @GetMapping("/folders/{id}")
    public ResponseEntity<List<PlanningFileViewModel>> getAllPlanningFilesByFolderId(@PathVariable("id") Long id) {
        return new ResponseEntity<>(planningFileService.getAllPlanningFilesByFolderId(id), HttpStatus.OK);
    }

    /**
     * This endpoint takes id of an existing planning file and new planning file name embedded in request body dto
     * and sends the renamed planning file.
     *
     * @param planningFileDto       {@link PlanningFileDto}
     * @param id                    {@link Long}
     * @return                      returns a planning file with the updated name
     */
//    @PatchMapping("/{id}/rename")
//    public ResponseEntity<PlanningFileViewModel> renamePlanningFile(@Valid @RequestBody PlanningFileDto planningFileDto,
//                                                                    @PathVariable("id") Long id) {
//        return new ResponseEntity<>(planningFileService.renamePlanningFile(planningFileDto, id), HttpStatus.OK);
//    }

    @PostMapping("/search")
    public ResponseEntity<List<PlanningFileViewModel>> getPlanningFilesBySearchKeyword(@RequestBody FileSearchDto
                                                                                               fileSearchDto) {
        return new ResponseEntity<>(planningFileService.getPlanningFilesBySearchKeyword(fileSearchDto), HttpStatus.OK);
    }

    //    @GetMapping("/search_match_string/{matchString}")
    //    public ResponseEntity<PlanningFileViewModel> getPlanningFileByMatchString(@PathVariable("matchString")
    //                                                                                        String matchString){
    //        return new ResponseEntity<>(planningFileService.getPlanningFileByMatchString(matchString), HttpStatus.OK);
    //    }

    @Transactional
    @PostMapping("/upload_file")
    public ResponseEntity<MessageResponse> create(@Valid @RequestBody List<PlanningFileDto> planningFileDto) {
        planningFileService.uploadFile(planningFileDto);
        return ResponseEntity.ok(new MessageResponse(CREATED_SUCCESSFULLY_MESSAGE));
    }

//    @PostMapping("/validate_match_string")
//    public ResponseEntity<MessageResponse> validateMatchString(@Valid @RequestBody List<ValidateMatchStringDto>
//                                                                       validateMatchStringDto) {
//        planningFileService.validateMatchString(validateMatchStringDto);
//        return ResponseEntity.ok(new MessageResponse(NO_DUPLICATE_MATCH_STRING));
//    }
}
