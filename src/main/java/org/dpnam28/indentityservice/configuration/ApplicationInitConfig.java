package org.dpnam28.indentityservice.configuration;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.dpnam28.indentityservice.entity.Role;
import org.dpnam28.indentityservice.entity.User;
import org.dpnam28.indentityservice.enums.Roles;
import org.dpnam28.indentityservice.repository.UserRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationInitConfig {

    PasswordEncoder encoder;
    @Bean
    @ConditionalOnProperty(prefix = "spring", value = "datasource.driver-class-name", havingValue = "com.mysql.cj.jdbc.Driver")
    ApplicationRunner applicationRunner(UserRepository userRepository){
        log.info("AppRunner");
        String password = "admin";
        Set<Role> roles = new HashSet<>();
        Role role  = Role.builder()
                .name(Roles.ADMIN.name())
                .description("Admin role")
                .build();
        roles.add(role);
        return args -> {
            if(userRepository.findByUsername("admin").isEmpty()){
            userRepository.save(User.builder()
                    .username("admin")
                    .password(encoder.encode(password))
                    .firstName("admin")
                    .lastName("admin")
                    .roles(roles)
                    .build());
            log.info("Admin account created successfully with password: {}", password);
            }
        };
    }
}
