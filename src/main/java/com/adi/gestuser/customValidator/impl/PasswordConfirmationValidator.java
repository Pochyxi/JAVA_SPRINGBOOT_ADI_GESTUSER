package com.adi.gestuser.customValidator.impl;

import com.adi.gestuser.customValidator.PasswordConfirmation;
import com.adi.gestuser.customValidator.PasswordMatches;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordConfirmationValidator implements ConstraintValidator<PasswordMatches, PasswordConfirmation> {
    @Override
    public void initialize( PasswordMatches constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid( PasswordConfirmation obj, ConstraintValidatorContext context) {
        return obj.getPassword().equals(obj.getConfirmPassword());
    }
}
