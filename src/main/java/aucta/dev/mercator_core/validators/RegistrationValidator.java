package aucta.dev.mercator_core.validators;

import aucta.dev.mercator_core.exceptions.BadRequestError;
import aucta.dev.mercator_core.exceptions.HttpException;
import aucta.dev.mercator_core.models.User;
import aucta.dev.mercator_core.auth.models.UserRegistrationRequest;
import aucta.dev.mercator_core.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class RegistrationValidator {

    @Autowired
    UserService userService;

    public void validateUserRegistration(UserRegistrationRequest userRegistrationRequest) throws HttpException {
        if (userRegistrationRequest == null)
            throw new BadRequestError("userRegistrationRequest must not be null");

        if (userRegistrationRequest.getUsername() == null || userRegistrationRequest.getUsername().isEmpty())
            throw new BadRequestError("Username is required field");

        if (userRegistrationRequest.getDateOfBirth() == null )
            throw new BadRequestError("Date of birth is required field");

        if (userRegistrationRequest.getPhoneNumber() == null || userRegistrationRequest.getPhoneNumber().isEmpty())
            throw new BadRequestError("Phone Number is required field");

        if (userRegistrationRequest.getFirstName() == null || userRegistrationRequest.getFirstName().isEmpty())
            throw new BadRequestError("First name is required field");

        if (userRegistrationRequest.getLastName() == null || userRegistrationRequest.getLastName().isEmpty())
            throw new BadRequestError("Last name is required field");

        if (userRegistrationRequest.getPassword() == null || userRegistrationRequest.getPassword().isEmpty())
            throw new BadRequestError("Password is required field");

        if (userRegistrationRequest.getConfirmPassword() == null || userRegistrationRequest.getConfirmPassword().isEmpty())
            throw new BadRequestError("confirmPassword is required field");

        User user = userService.getUserByUsername(userRegistrationRequest.getUsername());
        if (user != null) throw new BadRequestError("Username already exists");

        validatePasswords(userRegistrationRequest.getPassword(), userRegistrationRequest.getConfirmPassword());
    }

    public void validatePasswords(String password1, String password2) throws HttpException {
        if (password1 == null || password1.isEmpty()) {
            throw new BadRequestError("Password cannot be null or empty.");
        }

        if (!Pattern.matches("^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{6,}$", password1)) {
            throw new BadRequestError("Password must be at least 6 characters long and contain at least 1 uppercase letter, 1 number, and 1 special character.");
        }

        if (password2 == null || password2.isEmpty()) {
            throw new BadRequestError("Confirm password cannot be null or empty.");
        }

        if (!Pattern.matches("^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{6,}$", password2)) {
            throw new BadRequestError("Passwords must match.");
        }
    }
}
