package aucta.dev.mercator_core.validators;

import aucta.dev.mercator_core.auth.models.EditUserRequest;
import aucta.dev.mercator_core.exceptions.BadRequestError;
import aucta.dev.mercator_core.exceptions.HttpException;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class UserValidator {

    public void validateUpdateUserInfo(EditUserRequest request) throws HttpException {
        if (request.getUsername() == null || request.getUsername().isEmpty()) {
            throw new BadRequestError("Username cannot be null or empty.");
        }

        if (request.getEmail() == null || !Pattern.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", request.getEmail())) {
            throw new BadRequestError("Invalid email format.");
        }

        if (request.getFirstName() == null || request.getFirstName().isEmpty()) {
            throw new BadRequestError("First name cannot be null or empty.");
        }

        if (request.getLastName() == null || request.getLastName().isEmpty()) {
            throw new BadRequestError("Last name cannot be null or empty.");
        }
    }

    public void validateUpdatePassword(String password) throws HttpException {
        if (password == null || password.isEmpty()) {
            throw new BadRequestError("Password cannot be null or empty.");
        }

        if (!Pattern.matches("^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{6,}$", password)) {
            throw new BadRequestError("Password must be at least 6 characters long and contain at least 1 uppercase letter, 1 number, and 1 special character.");
        }
    }
}
