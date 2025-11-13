package org.dpnam28.indentityservice.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.dpnam28.indentityservice.entity.Role;
import org.dpnam28.indentityservice.validation.BirthConstraint;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
    String password;
    String firstName;
    String lastName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @BirthConstraint(min = 6, message = "INVALID_BIRTH")
    LocalDate birth;
    List<String> roles;

}
