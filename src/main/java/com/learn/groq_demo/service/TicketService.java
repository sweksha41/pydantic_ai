package com.learn.groq_demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learn.groq_demo.model.GroqResponse;
import com.learn.groq_demo.model.Ticket;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class TicketService {

    private final WebClient webClient;
    private final String apiKey;

    public TicketService(WebClient groqWebClient,
                         @Value("${groq.api.key}") String apiKey) {
        this.webClient = groqWebClient;
        this.apiKey = apiKey;
    }

    public Mono<Ticket> extractTicket(String text) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> body = getMessageBody(text);

        return webClient.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(GroqResponse.class)
                .map(response -> {
                    String json = response.choices().getFirst().message().content();
                    try {
                        return objectMapper.readValue(json, Ticket.class);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to parse Groq JSON response", e);
                    }
                });
    }

    private static @NonNull Map<String, Object> getMessageBody(String text) {
        String model = "openai/gpt-oss-120b";

        String systemPrompt = """
                Extract the personal information from the ticket strictly based on this schema.
                If some fields are missing like email then give missing error in error field and keep other fields blank.
                Return only valid JSON in this format:
                {
                  "name": "string",
                  "email": "string",
                  "issue": "string",
                  "error": "string"
                }
                """;

        return Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", text)
                ),
                "response_format", Map.of("type", "json_object")
        );
    }
}