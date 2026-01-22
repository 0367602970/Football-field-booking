package vti.group10.football_booking.service.geo;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GoogleGeocodingService implements GeocodingService {

    @Value("${google.maps.apiKey}")
    private String apiKey;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://maps.googleapis.com")
            .build();

    @Override
    public Optional<LatLng> geocode(String fullAddress) {
        try {
            String uri = UriComponentsBuilder.fromPath("/maps/api/geocode/json")
                    .queryParam("address", fullAddress)
                    .queryParam("key", apiKey)
                    .build(true) // encode
                    .toUriString();

            JsonNode root = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            if (root == null || !"OK".equals(root.path("status").asText())) return Optional.empty();

            JsonNode loc = root.path("results").get(0).path("geometry").path("location");
            return Optional.of(new LatLng(loc.path("lat").asDouble(), loc.path("lng").asDouble()));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
