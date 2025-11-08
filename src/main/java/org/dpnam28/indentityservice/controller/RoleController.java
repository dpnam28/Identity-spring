package org.dpnam28.indentityservice.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.dpnam28.indentityservice.dto.request.RoleRequest;
import org.dpnam28.indentityservice.dto.response.ApiResponse;
import org.dpnam28.indentityservice.dto.response.RoleResponse;
import org.dpnam28.indentityservice.service.RoleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {
    RoleService roleService;

    @PostMapping
    public ApiResponse<RoleResponse> createRole(@RequestBody RoleRequest request) {
        return roleService.createRole(request);
    }

    @GetMapping
    public ApiResponse<List<RoleResponse>> getAllRoles() {
        return roleService.getAllRoles();
    }

    @GetMapping("/{name}")
    public ApiResponse<RoleResponse> getRole(@PathVariable String name) {
        return roleService.getRole(name);
    }

    @DeleteMapping("/{name}")
    public ApiResponse<?> deleteRole(@PathVariable String name) {
        return roleService.deleteRole(name);
    }

    @PutMapping("/{name}")
    public ApiResponse<RoleResponse> updateRole(@PathVariable String name, @RequestBody RoleRequest request) {
        return roleService.updateRole(name.toUpperCase(), request);
    }


}
