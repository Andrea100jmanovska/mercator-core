package aucta.dev.mercator_core.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ActivateUserToken {
    @JsonProperty("userId")
    private String userId;
    @JsonProperty("validUntil")
    private Date validUntil;
}
