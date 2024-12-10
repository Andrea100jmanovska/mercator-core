package aucta.dev.mercator_core.models.dtos;

import aucta.dev.mercator_core.models.Organization;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UserDTO {

    private String id;

    private String username;

    private String firstName;

    private String lastName;

    private String email;

    private Boolean isEnabled;

    private Date dateCreated;

    private String twitterUrl;

    private String linkedInUrl;

    private String phoneNumber;

    private Date dateOfBirth;

    private Organization organization;
}
