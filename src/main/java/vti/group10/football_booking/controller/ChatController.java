package vti.group10.football_booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vti.group10.football_booking.dto.ChatRequestDTO;
import vti.group10.football_booking.dto.response.ClusterResponse;
import vti.group10.football_booking.dto.response.FieldResponse;
import vti.group10.football_booking.model.FieldCluster;
import vti.group10.football_booking.service.FieldClusterSearchService;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final FieldClusterSearchService clusterChatSearchService;

    @PostMapping
    public ResponseEntity<?> chat(@RequestBody ChatRequestDTO request) {
        try {
            String keyword = request.getMessage();
            if (keyword == null || keyword.isBlank()) {
                return ResponseEntity.ok(Collections.emptyList());
            }

            // Gọi service thông minh
            List<FieldCluster> clusters = clusterChatSearchService.searchByKeywordSmart(keyword);

            List<ClusterResponse> responses = clusters.stream()
                    .map(c -> {
                        List<FieldResponse> fields = c.getFields().stream()
                                .map(f -> FieldResponse.builder()
                                        .id(f.getId())
                                        .name(f.getName())
                                        .pricePerHour(f.getPricePerHour())
                                        .build())
                                .toList();

                        return ClusterResponse.builder()
                                .id(c.getId())
                                .name(c.getName())
                                .address(c.getAddress())
                                .district(c.getDistrict())
                                .city(c.getCity())
                                .latitude(c.getLatitude())
                                .longitude(c.getLongitude())
                                .ownerId(c.getOwner() != null ? c.getOwner().getId() : null)
                                .fields(fields)
                                .imageUrls(c.getImages() != null ? c.getImages().stream().map(i -> i.getImageUrl()).toList() : Collections.emptyList())
                                .build();
                    })
                    .toList();

            return ResponseEntity.ok(responses);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Collections.emptyList());
        }
    }
}
