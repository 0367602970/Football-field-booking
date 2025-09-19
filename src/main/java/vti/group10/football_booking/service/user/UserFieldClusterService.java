package vti.group10.football_booking.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vti.group10.football_booking.dto.response.ClusterResponse;
import vti.group10.football_booking.dto.response.FieldResponse;
import vti.group10.football_booking.model.FieldCluster;
import vti.group10.football_booking.model.FieldImage;
import vti.group10.football_booking.model.FootballField;
import vti.group10.football_booking.repository.FieldClusterRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserFieldClusterService {

    private final FieldClusterRepository clusterRepository;

    /**
     * Lấy danh sách tất cả cụm sân (kèm sân con & ảnh)
     */
    @Transactional(readOnly = true)
    public List<ClusterResponse> listClusters() {
        List<FieldCluster> clusters = clusterRepository.findAll();
        return clusters.stream().map(this::mapClusterToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ClusterResponse getClusterDetail(Integer clusterId) {
        return clusterRepository.findById(clusterId)
                .map(this::mapClusterToResponse)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<ClusterResponse> searchClusters(String keyword, int page, int size) {
        if (keyword == null || keyword.isBlank()) return listClusters();
        return clusterRepository.searchByKeyword(keyword, org.springframework.data.domain.PageRequest.of(page, size))
                .stream()
                .map(this::mapClusterToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ClusterResponse> filterClusters(String address, String district, String city) {
        List<FieldCluster> clusters = clusterRepository.filterByLocation(address, district, city);
        return clusters.stream().map(this::mapClusterToResponse).collect(Collectors.toList());
    }

    // ---------- mapping helper ----------
    private ClusterResponse mapClusterToResponse(FieldCluster cluster) {
        List<FieldResponse> fields = cluster.getFields() != null
                ? cluster.getFields().stream()
                .map(this::mapFieldToResponse)
                .toList()
                : Collections.emptyList();

        List<String> images = cluster.getImages() != null
                ? cluster.getImages().stream().map(FieldImage::getImageUrl).toList()
                : Collections.emptyList();

        return ClusterResponse.builder()
                .id(cluster.getId())
                .name(cluster.getName())
                .address(cluster.getAddress())
                .district(cluster.getDistrict())
                .city(cluster.getCity())
                .latitude(cluster.getLatitude())
                .longitude(cluster.getLongitude())
                .ownerId(cluster.getOwner() != null ? cluster.getOwner().getId() : null)
                .imageUrls(images)
                .fields(fields)
                .build();
    }

    private FieldResponse mapFieldToResponse(FootballField f) {
        return FieldResponse.builder()
                .id(f.getId())
                .name(f.getName())
                .description(f.getDescription())
                .pricePerHour(f.getPricePerHour())
                .status(f.getStatus() != null ? f.getStatus().name() : null)
                .clusterId(f.getCluster() != null ? f.getCluster().getId() : null)
                .clusterName(f.getCluster() != null ? f.getCluster().getName() : null)
                .build();
    }

}
