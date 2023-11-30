package com.digigate.engineeringmanagement.common.controller;
import com.digigate.engineeringmanagement.common.payload.request.RoleAccessDto;
import com.digigate.engineeringmanagement.common.payload.request.RoleDetailViewModel;
import com.digigate.engineeringmanagement.common.payload.request.RoleDto;
import com.digigate.engineeringmanagement.common.payload.request.RoleViewModel;
import com.digigate.engineeringmanagement.common.service.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;


/**
 * Role Controller
 */
@RestController
@RequestMapping("/api/role")
public class RoleController{
    private final RoleService roleService;

    /**
     * Autowired constructor
     *
     * @param roleService           {@link  RoleService}
     */
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * This is an API endpoint to save Role
     *
     * @param roleDto {@link RoleDto}
     * @return role id {@link Integer}
     */
    @PostMapping("/")
    public ResponseEntity<Integer>saveRole(@Valid @RequestBody RoleDto roleDto){
        return ResponseEntity.ok(roleService.save(roleDto,null));
    }

    /**
     * This is an API endpoint to update role
     *
     * @param roleDto                           {@link RoleDto}
     * @return newly updated role id            {@link  Integer}
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<Integer>updateRole(@Valid @RequestBody RoleDto roleDto, @PathVariable Integer id){
        return ResponseEntity.ok(roleService.save(roleDto,id));
    }

    /**
     * This is an API endpoint to duplicate a role
     *
     * @param roleDto                           {@link  RoleDto}
     * @param id                                {@link  Integer}
     * @return duplicated role id               {@link  Integer}
     */
    @PostMapping("/duplicate/{id}")
    public ResponseEntity<Integer>duplicateRole(@RequestBody RoleDto roleDto, @PathVariable Integer id){
        return ResponseEntity.ok(roleService.duplicate(roleDto,id));
    }


    /**
     * This is an API endpoint to get all roles
     *
     * @return list of roles            {@link List<RoleDetailViewModel>}
     */
    @GetMapping("/")
    public ResponseEntity<List<RoleDetailViewModel>>getAllRole(){
        return ResponseEntity.ok(roleService.getAll());
    }

    /**
     * This is an API endpoint to get role by id
     *
     * @param id            {@link  Integer}
     * @return role details {@link RoleDetailViewModel}
     */
    @GetMapping("/{id}")
    public ResponseEntity<RoleDetailViewModel>getRoleById(@PathVariable Integer id){
        return ResponseEntity.ok(roleService.getDetailsById(id));
    }


    /**
     * This is an API endpoint to delete role
     *
     * @param id {@link  Integer}
     * @return successfully deletion message {@link String}
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteRole(@PathVariable Integer id){
        return ResponseEntity.ok(roleService.delete(id));
    }



    /**
     * responsible for updating role access rights
     *
     * @param roleAccessDto {@link RoleAccessDto}
     * @return role access info as view model
     */
    @PutMapping("/update/access/rights")
    public ResponseEntity<RoleViewModel> updateProfileAccessRights(@Valid @RequestBody RoleAccessDto roleAccessDto) {
        RoleViewModel roleViewModel = roleService.updateRoleWithAccessRights(roleAccessDto);
        return new ResponseEntity<>(roleViewModel, HttpStatus.OK);
    }

    /**
     * responsible for providing specific role's access right
     *
     * @param roleId role id
     * @return role access info as view model
     */
    @GetMapping("/view/access/rights/{roleId}")
    public ResponseEntity<Map<Long, List<Integer>>> getRoleAccessRights(@PathVariable Integer roleId) {
        Map<Long, List<Integer>> roleAccessRightMap = roleService.getRoleAccessRights(roleId);
        return new ResponseEntity<>(roleAccessRightMap, HttpStatus.OK);
    }
}
