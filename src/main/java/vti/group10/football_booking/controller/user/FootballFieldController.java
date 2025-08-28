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

    // 2. Lấy chi tiết sân theo id
    @GetMapping("/{id}")
    public FootballFieldDetailResponse getFieldDetail(@PathVariable Integer id) {
        return service.getFieldById(id);
    }

    // 3. Tìm kiếm sân theo từ khóa (name hoặc location)
    @GetMapping("/search")
    public Page<FootballFieldResponse> searchFields(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return service.searchFields(keyword, pageable);
    }
}
