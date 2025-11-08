package org.dpnam28.indentityservice.mapper;

import org.dpnam28.indentityservice.dto.request.PermissionRequest;
import org.dpnam28.indentityservice.dto.response.PermissionResponse;
import org.dpnam28.indentityservice.entity.Permission;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);
    PermissionResponse toPermissionResponse(Permission permission);
}
