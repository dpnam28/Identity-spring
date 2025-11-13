package org.dpnam28.indentityservice.service;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.dpnam28.indentityservice.dto.request.UserCreationRequest;
import org.dpnam28.indentityservice.dto.response.ApiResponse;
import org.dpnam28.indentityservice.dto.response.UserResponse;
import org.dpnam28.indentityservice.entity.Role;
import org.dpnam28.indentityservice.entity.User;
import org.dpnam28.indentityservice.enums.Roles;
import org.dpnam28.indentityservice.exception.AppException;
import org.dpnam28.indentityservice.exception.ErrorCode;
import org.dpnam28.indentityservice.mapper.RoleMapper;
import org.dpnam28.indentityservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.Set;

@Slf4j
@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
public class UserServiceTest {

    @Autowired
    private UserService userService;
    @MockitoBean
    private UserRepository userRepository;

    private UserCreationRequest userCreationRequest;
    private User user;
    private ApiResponse<UserResponse> response;
    @Autowired
    private RoleMapper roleMapper;

    @BeforeEach
    void initData() {
        LocalDate birth = LocalDate.of(2000, 1, 1);
        Set<Role> roles = Set.of(Role.builder()
                .name(Roles.USER.name())
                .description(Roles.USER.getDescription())
                .build());
        userCreationRequest = UserCreationRequest.builder()
                .username("johndoe")
                .password("password")
                .firstName("John")
                .lastName("Doe")
                .birth(birth)
                .build();
        user = User.builder()
                .id("cf0600f538b3")
                .username("johndoe")
                .firstName("John")
                .lastName("Doe")
                .birth(birth)
                .roles(roles)
                .build();

        response = ApiResponse.<UserResponse>builder()
                .code(200)
                .message("Create user successfully")
                .result(UserResponse.builder()
                        .id("cf0600f538b3")
                        .username("johndoe")
                        .firstName("John")
                        .lastName("Doe")
                        .birth(birth)
                        .roles(Set.of(roleMapper.toRoleResponse(roles.iterator().next())))
                        .build()).build();

    }

    @Test
    void createUser_validRequest_success() {
        Mockito.when(userRepository.existsUserByUsername(ArgumentMatchers.any())).thenReturn(false);
        Mockito.when(userRepository.save(ArgumentMatchers.any())).thenReturn(user);

        ApiResponse<UserResponse> serviceResponse = userService.createUser(userCreationRequest);
        log.warn(response.toString());
        log.warn(serviceResponse.toString());
        Assertions.assertThat(serviceResponse.getCode()).isEqualTo(response.getCode());
        Assertions.assertThat(serviceResponse.getMessage()).isEqualTo(response.getMessage());
        Assertions.assertThat(serviceResponse.getResult()).isEqualTo(response.getResult());
    }

    @Test
    void createUser_userExisted_fail() {
        Mockito.when(userRepository.existsUserByUsername(ArgumentMatchers.any())).thenReturn(true);

        Assertions.assertThatThrownBy(() -> userService.createUser(userCreationRequest))
                .isInstanceOf(AppException.class)
                .hasMessage(ErrorCode.USER_EXISTED.getMessage());
    }
}
