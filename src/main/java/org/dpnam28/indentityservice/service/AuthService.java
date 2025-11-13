package org.dpnam28.indentityservice.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.dpnam28.indentityservice.dto.request.AuthRequest;
import org.dpnam28.indentityservice.dto.request.IntrospectRequest;
import org.dpnam28.indentityservice.dto.request.LogoutRequest;
import org.dpnam28.indentityservice.dto.request.RefreshTokenRequest;
import org.dpnam28.indentityservice.dto.response.ApiResponse;
import org.dpnam28.indentityservice.dto.response.AuthResponse;
import org.dpnam28.indentityservice.dto.response.IntrospectResponse;
import org.dpnam28.indentityservice.entity.InvalidatedToken;
import org.dpnam28.indentityservice.entity.User;
import org.dpnam28.indentityservice.exception.AppException;
import org.dpnam28.indentityservice.exception.ErrorCode;
import org.dpnam28.indentityservice.repository.InvalidatedTokenRepository;
import org.dpnam28.indentityservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService {

    UserRepository userRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;

    @NonFinal
    @Value("${jwt.secret-key}")
    private String SECRET_KEY;
    @NonFinal
    @Value("${jwt.expire-time}")
    private int EXPIRE_TIME;
    @NonFinal
    @Value("${jwt.refresh-token-expire-time}")
    private int REFRESH_TOKEN_EXPIRE_TIME;

    public ApiResponse<AuthResponse> authenticate(AuthRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        PasswordEncoder encoder = new BCryptPasswordEncoder(10);

        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);
        }
        String token = generateToken(user);

        return ApiResponse.<AuthResponse>builder()
                .code(200)
                .message("Authenticate successfully")
                .result(AuthResponse.builder()
                        .token(token)
                        .authenticated(true).build())
                .build();
    }

    public ApiResponse<IntrospectResponse> introspect(IntrospectRequest request) throws ParseException, JOSEException {
        String token = request.getToken();
        boolean isTokenValid;
        try {
            verifyToken(token, false);
            isTokenValid = true;
        } catch (AppException e) {
            isTokenValid = false;
        }
        return ApiResponse.<IntrospectResponse>builder()
                .code(200)
                .message("Introspect successfully")
                .result(IntrospectResponse.builder()
                        .valid(isTokenValid)
                        .build())
                .build();

    }

    public ApiResponse<?> logout(LogoutRequest request){
        try{
        SignedJWT signedJWT = verifyToken(request.getToken(), true);
        String jit = signedJWT.getJWTClaimsSet().getJWTID();
        Date expireTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        invalidatedTokenRepository.save(InvalidatedToken.builder()
                .id(jit)
                .expireTime(expireTime)
                .build());

        return ApiResponse.builder()
                .code(200)
                .message("Logout successfully")
                .build();

        } catch (JOSEException | ParseException e) {
            throw new AppException(ErrorCode.TOKEN_NOT_VALID);
        }
    }

    public ApiResponse<AuthResponse> refreshToken(RefreshTokenRequest token) throws ParseException, JOSEException {
        var signedJWT = verifyToken(token.getToken(), false);
        var jit = signedJWT.getJWTClaimsSet().getJWTID();
        var expireTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jit)
                .expireTime(expireTime)
                .build();
        invalidatedTokenRepository.save(invalidatedToken);

        var username = signedJWT.getJWTClaimsSet().getSubject();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        var generateToken = generateToken(user);
        return ApiResponse.<AuthResponse>builder()
                .code(200)
                .message("Refresh token successfully")
                .result(AuthResponse.builder()
                        .token(generateToken)
                        .authenticated(true)
                        .build())
                .build();
    }
    private SignedJWT verifyToken(String token, boolean isRefreshToken) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SECRET_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expireTime = isRefreshToken
                ? new Date(signedJWT.getJWTClaimsSet().getIssueTime()
                    .toInstant().plus(REFRESH_TOKEN_EXPIRE_TIME, ChronoUnit.SECONDS).toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();
        if (!(signedJWT.verify(verifier) && expireTime.after(new Date())) || invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
            throw new AppException(ErrorCode.TOKEN_NOT_VALID);
        }
        return signedJWT;
    }

    private String generateToken(User user) {
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("identity-service")
                .issueTime(new Date())
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .expirationTime(new Date(System.currentTimeMillis() + EXPIRE_TIME))
                .build();
        JWSObject jwsObject = new JWSObject(new JWSHeader(JWSAlgorithm.HS256), new Payload(claimsSet.toJSONObject()));

        try {
            jwsObject.sign(new MACSigner(SECRET_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    private String buildScope(User user) {
        StringJoiner joiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(user.getRoles())) {
            user.getRoles().forEach(role -> {
                joiner.add("ROLE_" + role.getName());
                if (!CollectionUtils.isEmpty(role.getPermissions())) {
                    role.getPermissions().forEach(permission -> joiner.add(permission.getName()));
                }
            });
        }
        return joiner.toString();
    }
}
