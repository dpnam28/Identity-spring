package org.dpnam28.indentityservice.mapper;

import org.dpnam28.indentityservice.dto.request.RoleRequest;
import org.dpnam28.indentityservice.dto.response.RoleResponse;
import org.dpnam28.indentityservice.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);

}
