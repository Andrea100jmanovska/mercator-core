package aucta.dev.mercator_core.auth.models;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class FacebookRequest implements Serializable {

    private String email;
    private String password;
    private String name;

}