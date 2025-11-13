package org.dpnam28.indentityservice.configuration;

import com.nimbusds.jose.JOSEException;
import org.dpnam28.indentityservice.dto.request.IntrospectRequest;
import org.dpnam28.indentityservice.exception.AppException;
import org.dpnam28.indentityservice.exception.ErrorCode;
import org.dpnam28.indentityservice.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;
import java.util.Objects;

@Configuration
public class CustomJwtDecoder implements JwtDecoder {

    @Value("${jwt.secret-key}")
    private String SECRET_KEY;

    @Autowired
    private AuthService authService;
    private NimbusJwtDecoder nimbusJwtDecoder;
    @Override
    public Jwt decode(String token) throws JwtException {
        try{
            var auth = authService.introspect(IntrospectRequest.builder().token(token).build());
            if(!auth.getResult().isValid()){
                throw new JwtException("Token not valid");
            }
        } catch (ParseException | JOSEException e) {
            throw new JwtException("Token not valid", e);
        }
        if(Objects.isNull(nimbusJwtDecoder)){
        SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "HS256");
            nimbusJwtDecoder = NimbusJwtDecoder
                    .withSecretKey(secretKeySpec)
                    .macAlgorithm(MacAlgorithm.HS256)
                    .build();
        }
        return nimbusJwtDecoder.decode(token);
    }
}
