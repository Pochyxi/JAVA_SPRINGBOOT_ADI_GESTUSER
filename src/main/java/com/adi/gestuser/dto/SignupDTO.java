package com.adi.gestuser.dto;

import com.adi.gestuser.exception.ErrorCodeList;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupDTO  {

    // Email: formato email
    // Size: minimo 6 caratteri, massimo 50
    @Email(message = ErrorCodeList.FORMATERROR)
    @Size(min = 6, max = 50, message = ErrorCodeList.SIZEERROR)
    private String email;

    // Size: minimo 3 caratteri, massimo 50
    // Pattern: solo lettere, numeri, punti, underscore e trattini
    @Size(min = 3, max = 50, message = ErrorCodeList.SIZEERROR)
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$",
            message = ErrorCodeList.FORMATERROR)
    private String username;


}
