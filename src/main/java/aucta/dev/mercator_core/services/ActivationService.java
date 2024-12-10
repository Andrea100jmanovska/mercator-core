package aucta.dev.mercator_core.services;

import aucta.dev.mercator_core.models.ActivateUserToken;
import aucta.dev.mercator_core.models.User;
import aucta.dev.mercator_core.utils.Crypto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class ActivationService {

    @Autowired
    private UserService userService;

    @Transactional
    public String activateUser(String token) {
        try {
            Crypto crypto = new Crypto();
            ObjectMapper mapper = new ObjectMapper();
            byte[] decodedBytes = Base64.getDecoder().decode(token);

// Convert the byte array back to a String
            String decodedToken = new String(decodedBytes, StandardCharsets.UTF_8);
            ActivateUserToken activateUserToken = mapper.readValue(crypto.decrypt(decodedToken), ActivateUserToken.class);

            User user = userService.getById(activateUserToken.getUserId());
            if (user == null) {
                return "Invalid activation link.";
            }

            if (user.getIsEnabled()) {
                return "User is already activated.";
            }

            user.setIsEnabled(Boolean.TRUE);
            userService.save(user);

            return "User activated successfully!";
        } catch (Exception e) {
            return "Activation failed.";
        }
    }
}
