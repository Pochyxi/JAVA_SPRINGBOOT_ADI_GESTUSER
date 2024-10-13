package com.adi.gestuser.customValidator;

import com.adi.gestuser.customValidator.impl.PasswordConfirmationValidator;
import com.adi.gestuser.exception.ErrorCodeList;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordConfirmationValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordMatches {

    String message() default ErrorCodeList.NOTSAMEPASSWORDS;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
