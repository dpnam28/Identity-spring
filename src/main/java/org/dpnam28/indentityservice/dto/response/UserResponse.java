package org.dpnam28.indentityservice.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.dpnam28.indentityservice.entity.Role;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String id;
    String username;
    String firstName;
    String lastName;
    LocalDate birth;
    Set<Role> roles;
}
