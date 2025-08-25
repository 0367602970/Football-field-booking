package vti.group10.football_booking.controller.user;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vti.group10.football_booking.model.FootballField;
import vti.group10.football_booking.service.user.FootballFieldSearchService;

import java.util.List;

@RestController
@RequestMapping("/api/fields")
@RequiredArgsConstructor
public class FieldSearchController {
    private final FootballFieldSearchService service;

    @GetMapping("/search")
    public List<FootballField> searchFields(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) FootballField.Status status,
            @RequestParam(required = false) Double pricePerHour
    ) {
        return service.search(name, location, status, pricePerHour);
    }
}
