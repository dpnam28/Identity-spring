package org.dpnam28.indentityservice.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.dpnam28.indentityservice.dto.request.RoleRequest;
import org.dpnam28.indentityservice.dto.response.ApiResponse;
import org.dpnam28.indentityservice.dto.response.RoleResponse;
import org.dpnam28.indentityservice.entity.Permission;
import org.dpnam28.indentityservice.entity.Role;
import org.dpnam28.indentityservice.exception.AppException;
import org.dpnam28.indentityservice.exception.ErrorCode;
import org.dpnam28.indentityservice.mapper.RoleMapper;
import org.dpnam28.indentityservice.repository.PermissionRepository;
import org.dpnam28.indentityservice.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService {
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    RoleMapper roleMapper;

    public ApiResponse<RoleResponse> createRole(RoleRequest request) {
        roleRepository.findById(request.getName()).ifPresent(item -> {
            throw new AppException(ErrorCode.ROLE_EXISTED);
        });
        Role role = roleMapper.toRole(request);
        List<Permission> permissionList = permissionRepository.findAllById(request.getPermissions());
        role.setPermissions(new HashSet<>(permissionList));
        log.info("Create role: {}", role.getName());
        roleRepository.save(role);
        return apiResponse("Create role successfully", roleMapper.toRoleResponse(role));
    }

    public ApiResponse<RoleResponse> updateRole(String name, RoleRequest request) {
        Role role = roleRepository.findById(name).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
        role.setName(name);
        role.setDescription(request.getDescription());
        List<Permission> permissionList = permissionRepository.findAllById(request.getPermissions());
        role.setPermissions(new HashSet<>(permissionList));
        roleRepository.save(role);
        return apiResponse("Update role successfully", roleMapper.toRoleResponse(role));
    }

    public ApiResponse<RoleResponse> getRole(String name) {
        Role role = roleRepository.findById(name).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
        return apiResponse("Get role successfully", roleMapper.toRoleResponse(role));
    }

    public ApiResponse<List<RoleResponse>> getAllRoles() {
        List<Role> roles = roleRepository.findAll();
        return apiResponse("Get all roles successfully", roles.stream().map(roleMapper::toRoleResponse).toList());
    }

    public ApiResponse<?> deleteRole(String name) {
        Role role = roleRepository.findById(name).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
        roleRepository.delete(role);
        return apiResponse("Delete role successfully", null);
    }

    private static <T> ApiResponse<T> apiResponse(String message, T data) {
        return ApiResponse.<T>builder()
                .code(200)
                .message(message)
                .result(data)
                .build();
    }
}
