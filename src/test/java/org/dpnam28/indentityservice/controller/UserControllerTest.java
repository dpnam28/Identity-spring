package org.dpnam28.indentityservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.dpnam28.indentityservice.dto.request.UserCreationRequest;
import org.dpnam28.indentityservice.dto.response.ApiResponse;
import org.dpnam28.indentityservice.dto.response.UserResponse;
import org.dpnam28.indentityservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:test.properties")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private UserCreationRequest userCreationRequest;
    private ApiResponse<UserResponse> response;

    @MockitoBean
    private UserService userService;

    @BeforeEach
    void initData() {
        LocalDate birth = LocalDate.of(2000, 1, 1);
        userCreationRequest = UserCreationRequest.builder()
                .username("johndoe")
                .password("password")
                .firstName("John")
                .lastName("Doe")
                .birth(birth)
                .build();

        response = ApiResponse.<UserResponse>builder()
                .code(200)
                .message("User created successfully")
                .result(UserResponse.builder()
                        .id("cf0600f538b3")
                        .username("john")
                        .firstName("John")
                        .lastName("Doe")
                        .birth(birth)
                        .build()).build();

    }

    @Test
    void createUser_success() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(userCreationRequest);

        Mockito.when(userService.createUser(ArgumentMatchers.any())).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value("200"));
    }
    @Test
    void createUser_usernameInvalid_fail() throws Exception {
        userCreationRequest.setUsername("john");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(userCreationRequest);

        Mockito.when(userService.createUser(ArgumentMatchers.any())).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value("400"))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Username must be at least 5 characters"));
    }

}
