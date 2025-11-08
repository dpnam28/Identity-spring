package org.dpnam28.indentityservice.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.dpnam28.indentityservice.dto.request.PermissionRequest;
import org.dpnam28.indentityservice.dto.response.ApiResponse;
import org.dpnam28.indentityservice.dto.response.PermissionResponse;
import org.dpnam28.indentityservice.entity.Permission;
import org.dpnam28.indentityservice.exception.AppException;
import org.dpnam28.indentityservice.exception.ErrorCode;
import org.dpnam28.indentityservice.mapper.PermissionMapper;
import org.dpnam28.indentityservice.repository.PermissionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionService {

    PermissionRepository permissionRepository;
    PermissionMapper  permissionMapper;

    public ApiResponse<PermissionResponse> createPermission(PermissionRequest request) {
        if (permissionRepository.existsByName(request.getName())) {
            throw new AppException(ErrorCode.PERMISSION_EXISTED);
        }
        Permission permission = permissionMapper.toPermission(request);
        permission = permissionRepository.save(permission);
        return apiResponse("Create permission successfully", permissionMapper.toPermissionResponse(permission));
    }

    public ApiResponse<List<PermissionResponse>> getAllPermissions() {
        List<Permission> permissions = permissionRepository.findAll();
        return apiResponse("Get all permissions successfully", permissions.stream().map(permissionMapper::toPermissionResponse).toList());
    }

    public ApiResponse<?> deletePermission(String name) {
        permissionRepository.findByName(name)
                .orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOT_FOUND));
        permissionRepository.deleteById(name);
        return apiResponse("Delete permission successfully", null);
    }

    public ApiResponse<PermissionResponse> updatePermission(String name, PermissionRequest request) {
        permissionRepository.findByName(name).orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOT_FOUND));
        Permission permission = permissionMapper.toPermission(request);
        permission = permissionRepository.save(permission);
        return apiResponse("Update permission successfully", permissionMapper.toPermissionResponse(permission));
    }

    private static <T> ApiResponse<T> apiResponse(String message, T result) {
        return ApiResponse.<T>builder()
                .code(200)
                .message(message)
                .result(result)
                .build();
    }
}
