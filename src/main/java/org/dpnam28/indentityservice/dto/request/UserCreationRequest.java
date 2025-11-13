package org.dpnam28.indentityservice.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.dpnam28.indentityservice.validation.BirthConstraint;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {

    @Size(min = 5, message = "USERNAME_NOT_VALID")
    String username;

    @Size(min = 5, message = "PASSWORD_NOT_VALID")
    String password;
    String firstName;
    String lastName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @BirthConstraint(min = 16, message = "INVALID_BIRTH")
    LocalDate birth;
}
