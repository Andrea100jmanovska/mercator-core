package aucta.dev.mercator_core.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class CaptchaService {

    @Value("${google.recaptcha.secret}")
    private String recaptchaSecret;

    private static final String RECAPTCHA_URL = "https://www.google.com/recaptcha/api/siteverify";

    public boolean verifyCaptcha(String captchaResponse) {
        if (captchaResponse == null || captchaResponse.isEmpty()) {
            return false;
        }

        RestTemplate restTemplate = new RestTemplate();

        String url = UriComponentsBuilder.fromHttpUrl(RECAPTCHA_URL)
                .queryParam("secret", recaptchaSecret)
                .queryParam("response", captchaResponse)
                .toUriString();

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, null, String.class);


        boolean success = response.getStatusCode() == HttpStatus.OK &&
                response.getBody().contains("\"success\": true");

        if (!success) {
            System.out.println("CAPTCHA validation failed.");
        }

        return success;
    }

}
