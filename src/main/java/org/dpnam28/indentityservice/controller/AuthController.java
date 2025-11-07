package org.dpnam28.indentityservice.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.dpnam28.indentityservice.dto.request.AuthRequest;
import org.dpnam28.indentityservice.dto.request.IntrospectRequest;
import org.dpnam28.indentityservice.dto.response.ApiResponse;
import org.dpnam28.indentityservice.dto.response.AuthResponse;
import org.dpnam28.indentityservice.dto.response.IntrospectResponse;
import org.dpnam28.indentityservice.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {
    AuthService authService;

    @PostMapping("/login")
    public ApiResponse<AuthResponse> logIn(@RequestBody AuthRequest request) {
        return authService.authenticate(request);
    }

    @PostMapping("/introspect")
    public ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request) {
        return authService.introspect(request);
    }
}
