package vti.group10.football_booking.service.user;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import vti.group10.football_booking.model.FootballField;
import vti.group10.football_booking.repository.FootballFieldRepository;
import vti.group10.football_booking.specification.FootballFieldSpecification;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FootballFieldSearchService {

    private final FootballFieldRepository repository;

    public List<FootballField> search(String name, String location, FootballField.Status status, Double pricePerHour) {
        Specification<FootballField> spec =
                FootballFieldSpecification.hasName(name)
                        .and(FootballFieldSpecification.hasLocation(location))
                        .and(FootballFieldSpecification.hasStatus(status))
                        .and(FootballFieldSpecification.priceLessThanOrEqual(pricePerHour));

        return repository.findAll(spec);
    }
}
