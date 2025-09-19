package vti.group10.football_booking.service.owner;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vti.group10.football_booking.dto.request.FieldRequest;
import vti.group10.football_booking.dto.response.FieldResponse;
import vti.group10.football_booking.model.FootballField;
import vti.group10.football_booking.model.FieldCluster;
import vti.group10.football_booking.repository.FootballFieldRepository;

@Service
@RequiredArgsConstructor
public class FootballFieldService {

    private final FootballFieldRepository fieldRepository;

    // Tạo sân con gắn vào cụm sân
    public FootballField createField(FieldRequest request, FieldCluster cluster) {
        FootballField field = FootballField.builder()
                .name(request.getName())
                .description(request.getDescription())
                .pricePerHour(request.getPricePerHour())
                .status(FootballField.Status.valueOf(request.getStatus()))
                .cluster(cluster) // gán cụm sân
                .build();
        return fieldRepository.save(field);
    }

    // Cập nhật sân con
    public FootballField updateField(int fieldId, FieldRequest request) {
        FootballField field = fieldRepository.findById(fieldId)
                .orElseThrow(() -> new RuntimeException("Field not found"));

        field.setName(request.getName());
        field.setDescription(request.getDescription());
        field.setPricePerHour(request.getPricePerHour());
        field.setStatus(FootballField.Status.valueOf(request.getStatus()));

        return fieldRepository.save(field);
    }

    // Xóa sân con
    public void deleteField(int fieldId) {
        fieldRepository.deleteById(fieldId);
    }

    // Map sang DTO: trả thông tin sân + cụm sân
    public FieldResponse mapFieldToResponse(FootballField field) {
        FieldCluster cluster = field.getCluster();
        return FieldResponse.builder()
                .id(field.getId())
                .name(field.getName())
                .description(field.getDescription())
                .pricePerHour(field.getPricePerHour())
                .status(field.getStatus().name())
                // Thông tin cụm sân
                .clusterId(cluster.getId())
                .clusterName(cluster.getName())
                .clusterAddress(cluster.getAddress())
                .clusterDistrict(cluster.getDistrict())
                .clusterCity(cluster.getCity())
                .build();
    }

    // Lấy danh sách sân con theo cụm sân
    public List<FieldResponse> listFieldsByCluster(int clusterId) {
        return fieldRepository.findByClusterId(clusterId).stream()
                .map(this::mapFieldToResponse)
                .collect(Collectors.toList());
    }

    // Search theo tên sân hoặc cụm sân
    public List<FieldResponse> searchFields(String keyword) {
        return fieldRepository.searchFields(keyword).stream()
                .map(this::mapFieldToResponse)
                .collect(Collectors.toList());
    }

    // ✅ Thêm filter theo giá và cụm sân cho Owner
    public List<FieldResponse> filterFieldsByPriceAndCluster(Double minPrice, Double maxPrice, String clusterName) {
        return fieldRepository.filterFieldsByPriceAndCluster(minPrice, maxPrice, clusterName).stream()
                .map(this::mapFieldToResponse)
                .collect(Collectors.toList());
    }
}
