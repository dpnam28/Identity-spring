package org.dpnam28.indentityservice.mapper;

import org.dpnam28.indentityservice.dto.request.UserCreationRequest;
import org.dpnam28.indentityservice.dto.request.UserUpdateRequest;
import org.dpnam28.indentityservice.dto.response.UserResponse;
import org.dpnam28.indentityservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
    UserResponse toUserResponse(User user);
    List<UserResponse> toUserResponse(List<User> users);
}
