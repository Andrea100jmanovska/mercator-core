package aucta.dev.mercator_core.chatGPT.controller;

import aucta.dev.mercator_core.chatGPT.dto.ChatGPTRequest;
import aucta.dev.mercator_core.chatGPT.dto.ChatGPTResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/bot")
public class CustomBotController {

    @Value("${openai.model}")
    private String model;

    @Value(("${openai.api.url}"))
    private String apiURL;

    @Autowired
    private RestTemplate template;

    @PostMapping("/chat")
    public ResponseEntity<?> chat(@RequestBody ChatGPTRequest request) {
        try {
            ChatGPTResponse chatGptResponse = template.postForObject(apiURL, request, ChatGPTResponse.class);
            if (chatGptResponse != null && !chatGptResponse.getChoices().isEmpty()) {
                return ResponseEntity.ok(chatGptResponse.getChoices().get(0).getMessage().getContent());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                        .body("No response received from OpenAI API");
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                        .body("API rate limit exceeded. Please try again later.");
            } else if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid API key or authentication error.");
            }
            return ResponseEntity.status(e.getStatusCode())
                    .body("OpenAI API error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error: " + e.getMessage());
        }
    }
}