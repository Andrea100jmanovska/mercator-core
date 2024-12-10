package aucta.dev.mercator_core.controllers;

import aucta.dev.mercator_core.auth.CustomUserDetails;
import aucta.dev.mercator_core.auth.JwtUtil;
import aucta.dev.mercator_core.auth.MyUserDetailsService;
import aucta.dev.mercator_core.auth.models.FacebookRequest;
import aucta.dev.mercator_core.models.Group;
import aucta.dev.mercator_core.models.Organization;
import aucta.dev.mercator_core.models.User;
import aucta.dev.mercator_core.repositories.UserRepository;
import aucta.dev.mercator_core.services.GroupService;
import aucta.dev.mercator_core.services.OrganizationService;
import aucta.dev.mercator_core.utils.Crypto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class FacebookAuthController {

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupService groupService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtTokenUtil;

    @Value("${app.base.url}")
    String APP_BASE_URL;

    @PostMapping("/facebook")
    public ResponseEntity<String> facebook(@RequestBody FacebookRequest request) throws Exception {
        String jwt = getOauthAccessTokenFacebook(request);
        return ResponseEntity.ok(jwt);
    }

    private String getOauthAccessTokenFacebook(FacebookRequest request) throws Exception {
        Crypto crypto = new Crypto();
        String email = request.getEmail();
        String firstName = request.getName().split(" ")[0];
        String lastName = request.getName().split(" ")[1];

        User user = userRepository.findFirstByEmail(email);
        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setUsername(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setPassword(passwordEncoder.encode(crypto.encrypt(email)));
            user.setIsEnabled(true);
            Organization organization = organizationService.getDefaultOrganization();
            user.setOrganization(organization);

            List<Group> groups = groupService.getUserGroupByOrganization(organization);
            user.setGroups(groups);
            user = userRepository.save(user);
        }
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user.getUsername(), crypto.encrypt(email)));

        final CustomUserDetails customUserDetails = userDetailsService.loadUserByUsername(user.getUsername());
        final String jwt = jwtTokenUtil.generateToken(customUserDetails);
        return jwt;
    }
}
