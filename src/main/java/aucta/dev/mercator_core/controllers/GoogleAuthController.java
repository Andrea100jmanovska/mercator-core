package aucta.dev.mercator_core.controllers;

import aucta.dev.mercator_core.auth.CustomUserDetails;
import aucta.dev.mercator_core.auth.JwtUtil;
import aucta.dev.mercator_core.auth.MyUserDetailsService;
import aucta.dev.mercator_core.models.Group;
import aucta.dev.mercator_core.models.Organization;
import aucta.dev.mercator_core.models.User;
import aucta.dev.mercator_core.repositories.UserRepository;
import aucta.dev.mercator_core.services.GroupService;
import aucta.dev.mercator_core.services.OrganizationService;
import aucta.dev.mercator_core.utils.Crypto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController()
public class GoogleAuthController {

    @Autowired
    private JwtUtil jwtTokenUtil;

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private GroupService groupService;

    @Value("${app.base.url}")
    String APP_BASE_URL;

    @Value("${core.base.url}")
    String CORE_BASE_URL;

    @Value("${client.id}")
    String client_id;

    @Value("${client.secret}")
    String client_secret;
    @Autowired
    private OrganizationService organizationService;

    @GetMapping("/grantcode")
    public String google(@RequestParam("code") String code,
                         @RequestParam("scope") String scope,
                         @RequestParam("authuser") String authUser,
                         @RequestParam("prompt") String prompt,
                         HttpServletResponse response) throws Exception {
        String jwt =  getOauthAccessTokenGoogle(code);
        response.sendRedirect( APP_BASE_URL+"/google-auth?token="+jwt);

        return jwt;
    }

    private String getOauthAccessTokenGoogle(String code) throws Exception {
        Crypto crypto = new Crypto();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("redirect_uri", CORE_BASE_URL+"/grantcode");
        params.add("client_id", client_id);
        params.add("client_secret", client_secret);
        params.add("scope",
                "https://www.googleapis.com/auth/userinfo.profile " +
                        "https://www.googleapis.com/auth/userinfo.email " +
                        "openid"
        );
        params.add("grant_type", "authorization_code");
        params.add("prompt","");

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, httpHeaders);

        String url = "https://oauth2.googleapis.com/token";
        String response = restTemplate.postForObject(url, requestEntity, String.class);
        JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
        String accessTokenValue = jsonObject.get("access_token").getAsString();
        JsonObject userDetails = getProfileDetailsGoogle(accessTokenValue);
        String email = userDetails.get("email").getAsString();
        String givenName = userDetails.get("given_name").getAsString();
        String familyName = userDetails.get("family_name").getAsString();
        User user = userRepository.findFirstByEmail(email);
        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setFirstName(givenName);
            user.setLastName(familyName);
            user.setUsername(email);
            user.setPassword(passwordEncoder.encode(crypto.encrypt(email)));
            user.setIsEnabled(true);
            Organization organization = organizationService.getDefaultOrganization();
            user.setOrganization(organization);

            List<Group> groups = groupService.getUserGroupByOrganization(organization);
            user.setGroups(groups);
            user=  userRepository.save(user);
        }

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user.getUsername(), crypto.encrypt(email)));

        final CustomUserDetails customUserDetails = userDetailsService
                .loadUserByUsername(user.getUsername());
        final String jwt = jwtTokenUtil.generateToken(customUserDetails);
        return jwt;
    }

    private JsonObject getProfileDetailsGoogle(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(accessToken);

        HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);

        String url = "https://www.googleapis.com/oauth2/v2/userinfo";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
        JsonObject jsonObject = new Gson().fromJson(response.getBody(), JsonObject.class);
        return jsonObject;
    }

}