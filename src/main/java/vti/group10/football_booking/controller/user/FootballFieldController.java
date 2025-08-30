package vti.group10.football_booking.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import vti.group10.football_booking.dto.response.FootballFieldResponse;
import vti.group10.football_booking.dto.response.FootballFieldDetailResponse;
import vti.group10.football_booking.service.user.FootballFieldService;

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
    // 4. Lọc theo city, district, pricePerHour (giá tối đa) – MỖI TRANG 20 SÂN
    @GetMapping("/filter")
    public Page<FootballFieldResponse> filterFields(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) Double pricePerHour,
            @RequestParam(defaultValue = "0") int page
    ) {
        city = (city != null && city.trim().isEmpty()) ? null : city;
        district = (district != null && district.trim().isEmpty()) ? null : district;

        Pageable pageable = PageRequest.of(page, 20);
        return service.filterFields(city, district, pricePerHour, pageable);
    }
}
