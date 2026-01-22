package vti.group10.football_booking.service;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
public class MapService {

    private final RestTemplate restTemplate;

    public MapService() {
        this.restTemplate = new RestTemplate();
        // Add interceptor to set a custom User-Agent
        this.restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().set("User-Agent", "MyFootballBookingApp/1.0 (email@example.com)");
            return execution.execute(request, body);
        });
    }

    public Map<String, Double> getCoordinates(String address, String district, String city) {
        String query = address + "," + district + "," + city;

        // Manually URL-encode the entire query string
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);

        // Build the URL using the correctly encoded query string
        String urlString = "https://nominatim.openstreetmap.org/search?q=" + encodedQuery + "&format=json&limit=1";
        URI url = URI.create(urlString);

        System.out.println("Calling Nominatim URL: " + url);

        ResponseEntity<List> response = restTemplate.exchange(
                url, HttpMethod.GET, null, List.class
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null && !response.getBody().isEmpty()) {
            Map<?, ?> first = (Map<?, ?>) response.getBody().get(0);
            Double lat = Double.valueOf((String) first.get("lat"));
            Double lon = Double.valueOf((String) first.get("lon"));
            return Map.of("lat", lat, "lng", lon);
        } else {
            throw new RuntimeException("No coordinates found for: " + query);
        }
    }
}