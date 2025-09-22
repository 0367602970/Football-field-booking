package vti.group10.football_booking.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vti.group10.football_booking.dto.response.FieldResponse;
import vti.group10.football_booking.model.FootballField;
import vti.group10.football_booking.repository.FootballFieldRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FootballFieldQueryService {

    private final FootballFieldRepository fieldRepository;

    // Xem tất cả sân con
    public List<FieldResponse> listAllFields() {
        return fieldRepository.findAll().stream()
                .filter(f -> f.getVisible() == FootballField.YesNo.YES)
                .map(this::mapFieldToResponse)
                .collect(Collectors.toList());
    }

    // Search theo từ khóa (tên field hoặc cluster name)
    public List<FieldResponse> searchFields(String keyword) {
        return fieldRepository.searchFields(keyword).stream()
                .map(this::mapFieldToResponse)
                .collect(Collectors.toList());
    }

    // ✅ Thêm filter theo minPrice, maxPrice, clusterName
    public List<FieldResponse> filterFields(Double minPrice, Double maxPrice, String clusterName) {
        return fieldRepository.filterFieldsByPriceAndCluster(minPrice, maxPrice, clusterName).stream()
                .map(this::mapFieldToResponse)
                .collect(Collectors.toList());
    }

    // Map FootballField sang DTO
    public FieldResponse mapFieldToResponse(FootballField field) {
        return FieldResponse.builder()
                .id(field.getId())
                .name(field.getName())
                .description(field.getDescription())
                .pricePerHour(field.getPricePerHour())
                .status(field.getStatus().name())
                .clusterName(field.getCluster().getName())
                .clusterAddress(field.getCluster().getAddress())
                .clusterDistrict(field.getCluster().getDistrict())
                .clusterCity(field.getCluster().getCity())
                .build();
    }
}
