package vti.group10.football_booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vti.group10.football_booking.dto.SearchCriteria;
import vti.group10.football_booking.model.FootballField;
import vti.group10.football_booking.repository.FootballFieldRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FootballFieldSearchService {

    private final FootballFieldRepository footballFieldRepository;

    public List<FootballField> search(SearchCriteria criteria) {
        // Nếu Gemini trả price, lấy khoảng ±10%
        Double price = criteria.getPrice() != null ? criteria.getPrice() : null;
        Double minPrice = criteria.getMinPrice() != null ? criteria.getMinPrice() : null;
        Double maxPrice = criteria.getMaxPrice() != null ? criteria.getMaxPrice() : null;

        if (price != null) {
            // ±10% tolerance
            minPrice = price * 0.9;
            maxPrice = price * 1.1;
        } else {
            if (minPrice != null && maxPrice == null) maxPrice = minPrice;
            if (maxPrice != null && minPrice == null) minPrice = maxPrice;
        }

        // Dùng repository filter
        Page<FootballField> page = footballFieldRepository.filterFootballFields(
                criteria.getCity(),
                criteria.getDistrict(),
                minPrice,
                maxPrice,
                Pageable.unpaged() // trả hết kết quả
        );

        return page.getContent();
    }
}
