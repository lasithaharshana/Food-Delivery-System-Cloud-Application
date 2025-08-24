package com.deliverysystem.authservice.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = RoleBasedValidationValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RoleBasedValidation {
    String message() default "Invalid role-based field configuration";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
