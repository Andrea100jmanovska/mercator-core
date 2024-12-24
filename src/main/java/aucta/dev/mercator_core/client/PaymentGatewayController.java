package aucta.dev.mercator_core.client;

import aucta.dev.mercator_core.auth.models.PaymentRequest;
import com.stripe.model.Charge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/payment")
public class PaymentGatewayController {

    private StripeClient stripeClient;

    @Autowired
    PaymentGatewayController(StripeClient stripeClient) {
        this.stripeClient = stripeClient;
    }

    @PostMapping("/charge")
    public Charge chargeCard(@RequestBody PaymentRequest paymentRequest) throws Exception {
        String token = paymentRequest.getToken();
        double amount = paymentRequest.getAmount();
        return this.stripeClient.chargeNewCard(token, amount);
    }
}