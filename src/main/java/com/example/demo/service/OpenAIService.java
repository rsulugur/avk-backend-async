package com.example.demo.service;


import com.example.demo.model.ChatResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class OpenAIService {

//    private final OpenAiChatModel chatModel;

    public List<String> extractProductNames(ChatResponse response) {
        List<String> productNames = new ArrayList<>();
        String regex = "\\{([^}]*)\\}";
        Pattern pattern = Pattern.compile(regex);
        String text = response.getChoices().get(0).getMessage().getContent();
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            productNames.add(matcher.group(1));
        }
        return productNames;
    }

    public Flux<String> queryFeatures(String message) {
        return getChatCompletion(message).flatMapIterable(this::extractProductNames);
    }

    public Mono<ChatResponse> getChatCompletion(String message) {
        WebClient webClient = getWebClient(WebClient.builder());
        String requestBody = """
                {
                    "model": "gpt-3.5-turbo",
                    "messages": [{"role": "system", "content": "Please Suggest 3 product names wrapped inside curly braces which best matches the user search query."}, {"role": "user", "content": "please suggest me e-commerce products which are related to %s"}]
                }
                """.formatted(message);

        return webClient.post()
                .uri("/v1/chat/completions")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(ChatResponse.class)
                .doOnError(throwable -> System.out.println(throwable.getMessage()));
    }

    public WebClient getWebClient(WebClient.Builder builder) {
        return builder.baseUrl("https://api.openai.com")
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Authorization", "Bearer SECRET TOKEN") // Replace with your API key
                .build();
    }
}

