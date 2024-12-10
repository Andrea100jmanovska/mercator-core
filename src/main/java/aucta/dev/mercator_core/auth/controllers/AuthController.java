package aucta.dev.mercator_core.auth.controllers;

import aucta.dev.mercator_core.auth.CustomUserDetails;
import aucta.dev.mercator_core.auth.MyUserDetailsService;
import aucta.dev.mercator_core.auth.models.AuthenticationRequest;
import aucta.dev.mercator_core.auth.models.AuthenticationResponse;
import aucta.dev.mercator_core.auth.JwtUtil;
import aucta.dev.mercator_core.models.User;
import aucta.dev.mercator_core.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtTokenUtil;

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    UserService userService;

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        try {
            User user = userService.getUserByUsername(authenticationRequest.getUsername());
            if (user == null) {
                return ResponseEntity.badRequest().body("Incorrect username or password");
            } else if (user.getIsEnabled() == null || !user.getIsEnabled()) {
                return ResponseEntity.badRequest().body("User is disabled");
            }
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
        final CustomUserDetails userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.getUsername());
        final String jwt = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

    @RequestMapping(value = "/validateToken", method = RequestMethod.GET)
    public Boolean validateToken(@RequestParam(value = "token") String token, @RequestParam(value = "username") String username) {
        final CustomUserDetails userDetails = userDetailsService
                .loadUserByUsername(username);
        return jwtTokenUtil.validateToken(token, userDetails);
    }
}