package org.dpnam28.indentityservice.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.dpnam28.indentityservice.dto.request.UserCreationRequest;
import org.dpnam28.indentityservice.dto.request.UserUpdateRequest;
import org.dpnam28.indentityservice.dto.response.ApiResponse;
import org.dpnam28.indentityservice.dto.response.UserResponse;
import org.dpnam28.indentityservice.entity.User;
import org.dpnam28.indentityservice.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;
    @PostMapping
    public ApiResponse<User> createUser(@RequestBody @Valid UserCreationRequest request){
        log.info("Create user controller");
        return userService.createUser(request);
    }

    @GetMapping
    public ApiResponse<List<UserResponse>> getAllUsers(){
    	return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable String id){
    	return userService.getUserById(id);
    }

    @GetMapping("/myinfo")
    public ApiResponse<UserResponse> getMyInfo(){
    	return userService.getMyInfo();
    }

    @PutMapping("/{id}")
    public ApiResponse<User> updateUser(@PathVariable String id, @RequestBody UserUpdateRequest request){
    	return userService.updateUser(id, request);
    }

    @DeleteMapping("{id}")
    public ApiResponse<?> deleteUser(@PathVariable String id){
        return userService.deleteUser(id);
    }
}
