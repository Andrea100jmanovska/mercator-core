package aucta.dev.mercator_core.controllers;

import aucta.dev.mercator_core.auth.models.UserRegistrationRequest;
import aucta.dev.mercator_core.models.*;
import aucta.dev.mercator_core.services.RegistrationService;
import aucta.dev.mercator_core.services.UserService;
import aucta.dev.mercator_core.validators.RegistrationValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/register/user")
public class RegisterUserController {
    @Autowired
    RegistrationService registrationService;

    @Autowired
    UserService userService;

    @Autowired
    RegistrationValidator registrationValidator;

    @RequestMapping(method = RequestMethod.POST)
    public User registerUser(@RequestBody UserRegistrationRequest userRegistrationRequest)
            throws Exception {
        registrationValidator.validateUserRegistration(userRegistrationRequest);
        return registrationService.registerUserNow(userRegistrationRequest);
    }

    @RequestMapping(path = "/forgotPassword", method = RequestMethod.PUT)
    public void forgotPassword(@RequestParam String email, @RequestParam String captchaResponse) throws Exception {
        userService.forgotPasswordRequest(email, captchaResponse);
    }

    @RequestMapping(path = "/forgotPasswordCheck", method = RequestMethod.GET)
    public Boolean forgotPasswordCheck(@RequestParam String token) throws Exception {
        return userService.checkIfPasswordRequestIsValid(token);
    }

    @RequestMapping(path = "/forgotPasswordReset", method = RequestMethod.PUT)
    public User forgotPasswordReset(@RequestParam String token, @RequestParam String password) throws Exception {
        return userService.forgotPasswordReset(token, password);
    }
}
