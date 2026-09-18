package com.epicode.chatapp.ai;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class OpenRouterClient {

    private final RestClient restClient;
    private final String model;

    public OpenRouterClient(RestClient openRouterRestClient, @Value("${openrouter.model}") String model) {
        this.restClient = openRouterRestClient;
        this.model = model;
    }

    public String complete(String systemPrompt, String userPrompt) {
        ChatRequest request = new ChatRequest(model, List.of(
                new ChatMessage("system", systemPrompt),
                new ChatMessage("user", userPrompt)
        ));

        ChatResponse response = restClient.post()
                .uri("/chat/completions")
                .body(request)
                .retrieve()
                .body(ChatResponse.class);

        if (response == null || response.choices() == null || response.choices().isEmpty()) {
            throw new IllegalStateException("Nessuna risposta ricevuta dal modello AI");
        }
        return response.choices().get(0).message().content().trim();
    }

    private record ChatRequest(String model, List<ChatMessage> messages) {
    }

    private record ChatMessage(String role, String content) {
    }

    private record ChatResponse(List<Choice> choices) {
    }

    private record Choice(ChatMessage message) {
    }
}
