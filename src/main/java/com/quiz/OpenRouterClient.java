package com.quiz;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public class OpenRouterClient {
    private static final String BASE_URL = "https://openrouter.ai/api/v1";
    private final OkHttpClient client;
    private final String apiKey;
    private final ObjectMapper objectMapper;

    public OpenRouterClient(String apiKey) {
        this.apiKey = apiKey;
        this.client = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public String createChatCompletion(String prompt) throws IOException {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        
        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);

        List<Map<String, Object>> messages = new ArrayList<>();
        messages.add(message);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "openai/gpt-3.5-turbo");
        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.9);
        requestBody.put("max_tokens", 4000);
        requestBody.put("frequency_penalty", 1.0);
        requestBody.put("presence_penalty", 1.0);

        String jsonBody = objectMapper.writeValueAsString(requestBody);

        Request request = new Request.Builder()
            .url(BASE_URL + "/chat/completions")
            .addHeader("Authorization", "Bearer " + apiKey)
            .addHeader("HTTP-Referer", "localhost")
            .addHeader("X-Title", "Software Engineering Quiz")
            .post(RequestBody.create(jsonBody, JSON))
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                System.out.println("Error response from OpenRouter: " + errorBody);
                throw new IOException("Unexpected response code: " + response.code() + "\n" + errorBody);
            }

            String responseBody = response.body().string();
            System.out.println("OpenRouter Response: " + responseBody);
            
            OpenRouterResponse completionResponse = objectMapper.readValue(responseBody, OpenRouterResponse.class);
            if (completionResponse.choices == null || completionResponse.choices.isEmpty()) {
                throw new IOException("No choices in response");
            }
            return completionResponse.choices.get(0).message.content;
        }
    }

    // Response classes for JSON deserialization
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class OpenRouterResponse {
        @JsonProperty("choices")
        public List<Choice> choices;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Choice {
        @JsonProperty("message")
        public Message message;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Message {
        @JsonProperty("content")
        public String content;
    }
} 