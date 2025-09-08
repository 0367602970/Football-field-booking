package vti.group10.football_booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vti.group10.football_booking.dto.ChatRequestDTO;
import vti.group10.football_booking.dto.SearchCriteria;
import vti.group10.football_booking.dto.response.FootballFieldResponse;
import vti.group10.football_booking.service.FootballFieldSearchService;
import vti.group10.football_booking.service.GeminiService;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final GeminiService geminiService;
    private final FootballFieldSearchService searchService;

    @PostMapping
    public ResponseEntity<List<FootballFieldResponse>> chat(@RequestBody ChatRequestDTO request) {
        try {
            // Lấy message từ client
            String userMessage = request.getMessage();

            // Gửi message cho GeminiService để trích xuất tiêu chí tìm sân
            SearchCriteria criteria = geminiService.extractCriteria(userMessage);

            // Nếu không có tiêu chí -> trả rỗng
            if (criteria == null
                    || (criteria.getCity() == null
                    && criteria.getMinPrice() == null
                    && criteria.getMaxPrice() == null)) {
                return ResponseEntity.ok(Collections.emptyList());
            }

            // Tìm sân trong DB theo tiêu chí
            List<FootballFieldResponse> results = searchService.search(criteria).stream().map(f ->
                    new FootballFieldResponse(
                            f.getId(),
                            f.getName(),
                            f.getAddress(),
                            f.getDistrict(),
                            f.getCity(),
                            f.getPricePerHour(),
                            f.getStatus().toString(),
                            List.of()
                    )
            ).toList();

            return ResponseEntity.ok(results);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Collections.emptyList());
        }
    }
}
