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
import org.dpnam28.indentityservice.dto.response.ApiResponse;
import org.dpnam28.indentityservice.dto.response.AuthResponse;
import org.dpnam28.indentityservice.dto.response.IntrospectResponse;
import org.dpnam28.indentityservice.entity.User;
import org.dpnam28.indentityservice.exception.AppException;
import org.dpnam28.indentityservice.exception.ErrorCode;
import org.dpnam28.indentityservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
import java.util.StringJoiner;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService {

    UserRepository repository;

    @NonFinal
    @Value("${jwt.secret-key}")
    private String SECRET_KEY;

    public ApiResponse<AuthResponse> authenticate(AuthRequest request) {
        User user = repository.findByUsername(request.getUsername())
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

    public ApiResponse<IntrospectResponse> introspect(IntrospectRequest request) {
        String token = request.getToken();
        try {
            JWSVerifier verifier = new MACVerifier(SECRET_KEY.getBytes());
            SignedJWT signedJWT = SignedJWT.parse(token);
            Date expireTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            return ApiResponse.<IntrospectResponse>builder()
                    .code(200)
                    .message("Introspect successfully")
                    .result(IntrospectResponse.builder()
                            .valid(signedJWT.verify(verifier) && expireTime.after(new Date()))
                            .build())
                    .build();
        } catch (JOSEException | ParseException e) {
            throw new AppException(ErrorCode.TOKEN_NOT_VALID);
        }
    }

    private String generateToken(User user) {
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("identity-service")
                .issueTime(new Date())
                .claim("scope", buildScope(user))
                .expirationTime(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .build();
        JWSObject jwsObject = new JWSObject(new JWSHeader(JWSAlgorithm.HS256), new Payload(claimsSet.toJSONObject()));

        try {
            jwsObject.sign(new MACSigner(SECRET_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    private String buildScope(User user){
        StringJoiner joiner = new StringJoiner(" ");
        user.getRoles().forEach(joiner::add);
        return joiner.toString();
    }
}
