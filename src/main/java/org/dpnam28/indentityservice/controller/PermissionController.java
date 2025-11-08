package org.dpnam28.indentityservice.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.dpnam28.indentityservice.dto.request.PermissionRequest;
import org.dpnam28.indentityservice.dto.response.ApiResponse;
import org.dpnam28.indentityservice.dto.response.PermissionResponse;
import org.dpnam28.indentityservice.service.PermissionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionController {
    PermissionService permissionService;

    @GetMapping
    public ApiResponse<List<PermissionResponse>> getAllPermissions() {
        return permissionService.getAllPermissions();
    }

    @PostMapping
    public ApiResponse<PermissionResponse> createPermission(@RequestBody PermissionRequest request) {
        return permissionService.createPermission(request);
    }

    @DeleteMapping("/{name}")
    public ApiResponse<?> deletePermission(@PathVariable String name) {
        return permissionService.deletePermission(name);
    }
}
