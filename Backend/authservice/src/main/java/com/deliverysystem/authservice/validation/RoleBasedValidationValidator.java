package com.deliverysystem.authservice.validation;

import com.deliverysystem.authservice.dto.RegisterRequest;
import com.deliverysystem.authservice.entity.User;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RoleBasedValidationValidator implements ConstraintValidator<RoleBasedValidation, RegisterRequest> {

    @Override
    public void initialize(RoleBasedValidation constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(RegisterRequest request, ConstraintValidatorContext context) {
        if (request == null || request.getRole() == null) {
            return true; // Let other validators handle null checks
        }

        context.disableDefaultConstraintViolation();

        // Address is required for all users
        if (request.getAddress() == null || request.getAddress().trim().isEmpty()) {
            context.buildConstraintViolationWithTemplate("Address is required")
                    .addPropertyNode("address")
                    .addConstraintViolation();
            return false;
        }
        
        if (request.getRole() == User.Role.RESTAURANT) {
            // Restaurant role must have restaurant name
            if (request.getRestaurantName() == null || request.getRestaurantName().trim().isEmpty()) {
                context.buildConstraintViolationWithTemplate("Restaurant name is required for restaurant role")
                        .addPropertyNode("restaurantName")
                        .addConstraintViolation();
                return false;
            }
        } else if (request.getRole() == User.Role.CUSTOMER) {
            // Customer role should not have restaurant name
            if (request.getRestaurantName() != null && !request.getRestaurantName().trim().isEmpty()) {
                context.buildConstraintViolationWithTemplate("Restaurant name should not be provided for customer role")
                        .addPropertyNode("restaurantName")
                        .addConstraintViolation();
                return false;
            }
        }

        return true;
    }
}
