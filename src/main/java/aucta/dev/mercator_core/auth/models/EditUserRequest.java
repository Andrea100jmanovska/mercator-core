package aucta.dev.mercator_core.auth.models;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class EditUserRequest implements Serializable {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private Date dateOfBirth;
    private String phoneNumber;
    private String twitterUrl;
    private String linkedInUrl;
}