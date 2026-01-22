package vti.group10.football_booking.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchCriteria {
    private String city;
    private String district;
    private String address;
    private Double minPrice;
    private Double maxPrice;
    private String status;
    private Double price;
    private String clusterName;

    // getters & setters
}