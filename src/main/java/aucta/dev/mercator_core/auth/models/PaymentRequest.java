package aucta.dev.mercator_core.auth.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequest {
    private String token;
    private double amount;
}
