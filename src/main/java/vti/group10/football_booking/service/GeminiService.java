package vti.group10.football_booking.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;
import vti.group10.football_booking.dto.SearchCriteria;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SearchCriteria extractCriteria(String userMessage) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + geminiApiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String promptText = "Extract city and price from the following Vietnamese message. "
                + "Return only JSON with keys city and price, no code block, no extra text. "
                + "Examples: 'tìm sân ở Hà Nội giá 200k' -> {\"city\": \"Hà Nội\", \"price\": \"200000\"}, "
                + "'tìm sân ở thành phố Hồ Chí Minh giá khoảng 300000' -> {\"city\": \"Hồ Chí Minh\", \"price\": \"300000\"}. "
                + "Now extract from: '" + userMessage + "'";

        Map<String, Object> part = new HashMap<>();
        part.put("text", promptText);

        Map<String, Object> content = new HashMap<>();
        content.put("parts", Collections.singletonList(part));

        Map<String, Object> body = new HashMap<>();
        body.put("contents", Collections.singletonList(content));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            // ---- PRINT JSON RAW ----
            System.out.println("=== Gemini RAW Response ===");
            System.out.println(response.getBody());
            System.out.println("==========================");

            // Parse response
            JsonNode root = objectMapper.readTree(response.getBody());
            String text = root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();

            return objectMapper.readValue(text, SearchCriteria.class);

        } catch (RestClientException e) {
            throw new RuntimeException("Gemini API error: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Parse Gemini response error: " + e.getMessage(), e);
        }
    }

}
