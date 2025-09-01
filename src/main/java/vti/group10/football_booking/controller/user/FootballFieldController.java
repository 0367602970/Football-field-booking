package vti.group10.football_booking.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import vti.group10.football_booking.dto.response.FootballFieldResponse;
import vti.group10.football_booking.dto.response.FootballFieldDetailResponse;
import vti.group10.football_booking.service.user.FootballFieldService;
import java.util.List;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/fields")
@RequiredArgsConstructor
public class FootballFieldController {

    private final FootballFieldService service;

    // 1. Lấy danh sách sân (có phân trang)
    @GetMapping
    public Page<FootballFieldResponse> getFields(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return service.getAllFields(pageable);
    }

    // 2. Lấy chi tiết sân theo id (hiển thị tối đa 8 ảnh)
    @GetMapping("/{id}")
    public FootballFieldDetailResponse getFieldDetail(@PathVariable Integer id) {
        return service.getFieldById(id);
    }

    // 3. Tìm kiếm sân theo từ khóa (name, address, district, city)
    @GetMapping("/search")
    public Page<FootballFieldResponse> searchFields(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return service.searchFields(keyword, pageable);
    }
    // 4. Lọc theo city, district, priceRange (1/2/3). Mỗi trang 20 sân.
    @GetMapping("/filter")
    public Page<FootballFieldResponse> filterFields(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) Integer priceRange, // 1,2,3 or null
            @RequestParam(defaultValue = "0") int page
    ) {
        // Chuẩn hóa empty string -> null
        city = (city != null && city.trim().isEmpty()) ? null : city;
        district = (district != null && district.trim().isEmpty()) ? null : district;

        Double minPrice = null;
        Double maxPrice = null;

        if (priceRange != null) {
            switch (priceRange) {
                case 1:
                    minPrice = 0.0;          // hoặc 1000.0 nếu bạn không muốn 0
                    maxPrice = 200000.0;
                    break;
                case 2:
                    minPrice = 201000.0;
                    maxPrice = 400000.0;
                    break;
                case 3:
                    minPrice = 401000.0;
                    maxPrice = 600000.0;
                    break;
                default:
                    // nếu muốn, trả 400 Bad Request cho giá trị ko hợp lệ
                    minPrice = null;
                    maxPrice = null;
            }
        }

        Pageable pageable = PageRequest.of(page, 20);
        return service.filterFields(city, district, minPrice, maxPrice, pageable);
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<FootballFieldService.NearbyFieldResponse>> nearby(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "5") double radiusKm) {
        return ResponseEntity.ok(service.findNearbyFields(lat, lng, radiusKm));
    }


}
