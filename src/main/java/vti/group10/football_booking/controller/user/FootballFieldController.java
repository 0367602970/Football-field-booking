package vti.group10.football_booking.controller.user;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import vti.group10.football_booking.dto.response.FootballFieldResponse;
import vti.group10.football_booking.service.user.FootballFieldService;
import vti.group10.football_booking.dto.response.FootballFieldDetailResponse;
@RestController
@RequestMapping("/api/fields")
@RequiredArgsConstructor
public class FootballFieldController {

    private final FootballFieldService service;

    @GetMapping
    public Page<FootballFieldResponse> getFields(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return service.getAllFields(pageable);
    }
    // API lấy chi tiết sân theo id
    @GetMapping("/{id}")
    public FootballFieldDetailResponse getFieldDetail(@PathVariable Integer id) {
        return service.getFieldById(id);
    }
}
