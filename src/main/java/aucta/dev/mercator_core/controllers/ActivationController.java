package aucta.dev.mercator_core.controllers;

import aucta.dev.mercator_core.services.ActivationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/activate-user")
public class ActivationController {

    @Autowired
    private ActivationService activationService;

    @GetMapping
    public ResponseEntity<String> activateUser(@RequestParam("token") String token) {
        String message = activationService.activateUser(token);
        if (message.equals("Activation failed.")) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
        }
        return ResponseEntity.ok(message);
    }
}
