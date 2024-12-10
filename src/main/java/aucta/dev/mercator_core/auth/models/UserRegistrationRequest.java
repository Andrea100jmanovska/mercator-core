package aucta.dev.mercator_core.auth.models;

import aucta.dev.mercator_core.models.Organization;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UserRegistrationRequest {

    private String username;
    private String email;
    private Organization organization;
    private String firstName;
    private String lastName;
    private Date dateOfBirth;
    private String phoneNumber;
    private String password;
    private String confirmPassword;
    private String captchaResponse;
}
