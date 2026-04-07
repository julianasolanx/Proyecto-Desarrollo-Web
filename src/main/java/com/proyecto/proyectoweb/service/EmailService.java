package com.proyecto.proyectoweb.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    @Value("${mailtrap.inbox}")
    private String inboxId;

    @Value("${mailtrap.token}")
    private String apiToken;

    private final RestTemplate restTemplate = new RestTemplate();

    public void enviarCorreo(String to, String subject, String message) {

        String url = "https://sandbox.api.mailtrap.io/api/send/" + inboxId;

        Map<String, Object> body = new HashMap<>();

        body.put("from", Map.of(
                "email", "test@mailtrap.io",
                "name", "Test App"
        ));

        body.put("to", List.of(
                Map.of("email", to)
        ));

        body.put("subject", subject);
        body.put("text", message);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Api-Token", apiToken);

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(body, headers);

        restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                String.class
        );
    }
}
