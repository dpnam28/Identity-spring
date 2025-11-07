package org.dpnam28.indentityservice.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.dpnam28.indentityservice.dto.request.UserCreationRequest;
import org.dpnam28.indentityservice.dto.request.UserUpdateRequest;
import org.dpnam28.indentityservice.dto.response.ApiResponse;
import org.dpnam28.indentityservice.dto.response.UserResponse;
import org.dpnam28.indentityservice.entity.User;
import org.dpnam28.indentityservice.enums.Roles;
import org.dpnam28.indentityservice.exception.AppException;
import org.dpnam28.indentityservice.exception.ErrorCode;
import org.dpnam28.indentityservice.mapper.UserMapper;
import org.dpnam28.indentityservice.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder encoder;

    public ApiResponse<User> createUser(UserCreationRequest request) {

        if (userRepository.existsUserByUsername(request.getUsername()))
            throw new AppException(ErrorCode.USER_EXISTED);

        User user = userMapper.toUser(request);
        user.setPassword(encoder.encode(request.getPassword()));
        HashSet<String> roles = new HashSet<>();
        roles.add(Roles.USER.toString());
        user.setRoles(roles);
        return ApiResponse.<User>builder()
                .code(200)
                .message("Create user successfully")
                .result(userRepository.save(user))
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<UserResponse>> getAllUsers() {
        log.info("Get all users");
        return ApiResponse.<List<UserResponse>>builder()
                .code(200)
                .message("Get all users successfully")
                .result(userMapper.toUserResponse(userRepository.findAll()))
                .build();
    }

    @PostAuthorize("returnObject.username == authentication.name")
    public UserResponse getUserById(String id) {
        log.info("Get user by id: {}", SecurityContextHolder.getContext().getAuthentication());
        return userMapper.toUserResponse(userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND)));
    }

    public ApiResponse<UserResponse> getMyInfo(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        return ApiResponse.<UserResponse>builder()
                .code(200)
                .message("Get my info successfully")
                .result(userMapper.toUserResponse(userRepository.findByUsername(name)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND))))
                .build();
    }

    public User updateUser(String id, UserUpdateRequest request) {
        User user = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Set<String> roles = request.getRoles();
        log.info(roles.toString());
        userMapper.updateUser(user, request);
        return userRepository.save(user);
    }

    public ApiResponse<?> deleteUser(String id) {
        userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        userRepository.deleteById(id);
        return ApiResponse.builder()
                .code(200)
                .message("Delete successfully")
                .build();
    }
}
