package vti.group10.football_booking.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vti.group10.football_booking.dto.response.FieldResponse;
import vti.group10.football_booking.service.user.FootballFieldQueryService;

import java.util.List;

@RestController
@RequestMapping("/user/fields")
@RequiredArgsConstructor
public class UserFootballFieldController {

    private final FootballFieldQueryService queryService;

    @GetMapping
    public ResponseEntity<List<FieldResponse>> listAll() {
        return ResponseEntity.ok(queryService.listAllFields());
    }

    @GetMapping("/search")
    public ResponseEntity<List<FieldResponse>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(queryService.searchFields(keyword));
    }

    // 🔥 Lọc theo priceRange (1,2,3) + clusterName
    @GetMapping("/filter")
    public ResponseEntity<List<FieldResponse>> filter(
            @RequestParam(required = false) Integer priceRange,
            @RequestParam(required = false) String clusterName
    ) {
        Double minPrice = null;
        Double maxPrice = null;

        if (priceRange != null) {
            switch (priceRange) {
                case 1: // 1000 - 200000
                    minPrice = 1000.0;
                    maxPrice = 200000.0;
                    break;
                case 2: // 201000 - 400000
                    minPrice = 201000.0;
                    maxPrice = 400000.0;
                    break;
                case 3: // 401000 - 600000
                    minPrice = 401000.0;
                    maxPrice = 600000.0;
                    break;
            }
        }

        return ResponseEntity.ok(queryService.filterFields(minPrice, maxPrice, clusterName));
    }
}
