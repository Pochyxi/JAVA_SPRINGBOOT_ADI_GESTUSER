package com.adi.gestuser.dto;


import com.adi.gestuser.customValidator.PasswordConfirmation;
import com.adi.gestuser.customValidator.PasswordMatches;
import com.adi.gestuser.exception.ErrorCodeList;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@PasswordMatches
public class ChangePasswordDTO implements PasswordConfirmation {

    private String usernameOrEmail;

    private String oldPassword;

    // Size: minimo 8 caratteri, massimo 20
    // Pattern: almeno una lettera maiuscola, una minuscola, un numero e un carattere speciale
    @Size(min = 8,max = 20, message = ErrorCodeList.SIZEERROR)
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=])(?=\\S+$).{8,}$",
            message = ErrorCodeList.FORMATERROR)
    private String newPassword;

    private String confirmPassword;

    @Override
    public String getPassword() {
        return newPassword;
    }

    @Override
    public String getConfirmPassword() {
        return confirmPassword;
    }
}
