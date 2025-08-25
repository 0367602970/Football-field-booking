package vti.group10.football_booking.specification;

import org.springframework.data.jpa.domain.Specification;
import vti.group10.football_booking.model.FootballField;

public class FootballFieldSpecification {

    public static Specification<FootballField> hasName(String name) {
        return (root, query, builder) ->
                name == null ? null : builder.like(builder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<FootballField> hasLocation(String location) {
        return (root, query, builder) ->
                location == null ? null : builder.like(builder.lower(root.get("location")), "%" + location.toLowerCase() + "%");
    }

    public static Specification<FootballField> hasStatus(FootballField.Status status) {
        return (root, query, builder) ->
                status == null ? null : builder.equal(root.get("status"), status);
    }

    public static Specification<FootballField> priceLessThanOrEqual(Double price) {
        return (root, query, builder) ->
                price == null ? null : builder.lessThanOrEqualTo(root.get("pricePerHour"), price);
    }
}
