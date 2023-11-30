package com.digigate.engineeringmanagement.planning.controller;

import com.digigate.engineeringmanagement.planning.entity.EngineModelType;
import com.digigate.engineeringmanagement.planning.payload.request.EngineModelTypeDto;
import com.digigate.engineeringmanagement.planning.service.EngineModelTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/engine/model/type")
public class EngineModelTypeController {
    private final EngineModelTypeService engineModelTypeService;

    public EngineModelTypeController(EngineModelTypeService engineModelTypeService) {
        this.engineModelTypeService = engineModelTypeService;
    }

    /**
     * This is an API endpoint to get Engine Model Type by id
     *
     * @param id {@link  Integer}
     */
    @GetMapping("/{id}")
    public ResponseEntity<EngineModelType> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(engineModelTypeService.findById(id));
    }

    /**
     * This is an API endpoint to save Engine Model Type
     *
     * @param engineModelTypeDto {@link EngineModelTypeDto}
     * @return Engine Model Type Id {@link Integer}
     */
    @PostMapping("/save")
    public ResponseEntity<Integer> save(@Valid @RequestBody EngineModelTypeDto engineModelTypeDto) {
        return ResponseEntity.ok(engineModelTypeService.save(engineModelTypeDto));
    }

    /**
     * This is an API endpoint to update Engine Model Type
     *
     * @param engineModelTypeDto {@link EngineModelTypeDto}
     * @return newly updated role id            {@link  Integer}
     */
    @PutMapping("/update")
    public ResponseEntity<Integer> update(@Valid @RequestBody EngineModelTypeDto engineModelTypeDto) {
        return ResponseEntity.ok(engineModelTypeService.update(engineModelTypeDto));
    }

    /**
     * This is an API endpoint to update Engine Model Type
     *
     * @param id {@link Integer}
     * @return Engine Model Type Id {@link  Integer}
     */
    @PutMapping("/status/toggle/{id}")
    public ResponseEntity<Integer> toggle(@PathVariable Integer id) {
        return ResponseEntity.ok(engineModelTypeService.toggleStatus(id));
    }

    /**
     * Get the list of Engine Model Type
     */
    @GetMapping("/list")
    public ResponseEntity<List<EngineModelType>> list() {
        return ResponseEntity.ok(engineModelTypeService.list());
    }
}