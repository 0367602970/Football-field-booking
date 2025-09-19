package vti.group10.football_booking.service.owner;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vti.group10.football_booking.dto.request.FieldClusterRequest;
import vti.group10.football_booking.dto.response.ClusterResponse;
import vti.group10.football_booking.dto.response.FieldResponse;
import vti.group10.football_booking.model.FieldCluster;
import vti.group10.football_booking.model.FieldImage;
import vti.group10.football_booking.model.FootballField;
import vti.group10.football_booking.model.User;
import vti.group10.football_booking.repository.FieldClusterRepository;
import vti.group10.football_booking.repository.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OwnerFieldClusterService {

    private final FieldClusterRepository clusterRepository;
    private final UserRepository userRepository;

    private User getCurrentOwner() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Owner not found with email: " + email));
    }

    // Lấy cluster theo ID (và check quyền sở hữu)
    public FieldCluster getClusterById(int clusterId) {
        User currentOwner = getCurrentOwner();
        FieldCluster cluster = clusterRepository.findById(clusterId)
                .orElseThrow(() -> new RuntimeException("Cluster not found with ID: " + clusterId));

        if (!cluster.getOwner().getId().equals(currentOwner.getId())) {
            throw new RuntimeException("Access denied");
        }
        return cluster;
    }

    // Tạo cluster mới kèm ảnh và sân con
    public FieldCluster createCluster(FieldClusterRequest request) {
        User owner = getCurrentOwner();

        FieldCluster cluster = FieldCluster.builder()
                .name(request.getName())
                .address(request.getAddress())
                .district(request.getDistrict())
                .city(request.getCity())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .owner(owner)
                .images(new HashSet<>())  // tránh null
                .fields(new HashSet<>())  // tránh null
                .build();

        // Thêm hình ảnh (nếu có)
        if (request.getImages() != null) {
            request.getImages().forEach(imgReq ->
                    cluster.getImages().add(FieldImage.builder()
                            .imageUrl(imgReq.getImageUrl())
                            .cluster(cluster)
                            .build())
            );
        }

        // Thêm sân con (nếu có)
        if (request.getFields() != null) {
            request.getFields().forEach(fReq ->
                    cluster.getFields().add(FootballField.builder()
                            .name(fReq.getName())
                            .description(fReq.getDescription())
                            .pricePerHour(fReq.getPricePerHour())
                            .status(FootballField.Status.valueOf(fReq.getStatus()))
                            .cluster(cluster)
                            .build())
            );
        }

        return clusterRepository.save(cluster);
    }

    // Cập nhật cluster (chỉ owner của cluster được update)
    public FieldCluster updateCluster(int clusterId, FieldClusterRequest request) {
        FieldCluster cluster = getClusterById(clusterId); // check quyền rồi

        cluster.setName(request.getName());
        cluster.setAddress(request.getAddress());
        cluster.setDistrict(request.getDistrict());
        cluster.setCity(request.getCity());
        cluster.setLatitude(request.getLatitude());
        cluster.setLongitude(request.getLongitude());

        return clusterRepository.save(cluster);
    }

    // Xóa cluster (chỉ owner được xóa)
    public void deleteCluster(int clusterId) {
        FieldCluster cluster = getClusterById(clusterId); // check quyền rồi
        clusterRepository.delete(cluster);
    }

    // Thêm ảnh vào cluster
    public FieldCluster addImageToCluster(int clusterId, String imageUrl) {
        FieldCluster cluster = getClusterById(clusterId); // check quyền rồi
        cluster.getImages().add(FieldImage.builder()
                .imageUrl(imageUrl)
                .cluster(cluster)
                .build());
        return clusterRepository.save(cluster);
    }

    // Map cluster → ClusterResponse (có fields + images)
    public ClusterResponse mapClusterToResponse(FieldCluster cluster) {
        List<FieldResponse> fields = cluster.getFields() != null
                ? cluster.getFields().stream()
                .map(f -> FieldResponse.builder()
                        .id(f.getId())
                        .name(f.getName())
                        .description(f.getDescription())
                        .pricePerHour(f.getPricePerHour())
                        .status(f.getStatus().name())
                        .clusterId(cluster.getId())
                        .clusterName(cluster.getName())
                        .build())
                .collect(Collectors.toList())
                : List.of();

        List<String> imageUrls = cluster.getImages() != null
                ? cluster.getImages().stream().map(FieldImage::getImageUrl).collect(Collectors.toList())
                : List.of();

        Double minPrice = fields.stream().map(FieldResponse::getPricePerHour).min(Double::compare).orElse(null);
        Double maxPrice = fields.stream().map(FieldResponse::getPricePerHour).max(Double::compare).orElse(null);

        return ClusterResponse.builder()
                .id(cluster.getId())
                .name(cluster.getName())
                .address(cluster.getAddress())
                .district(cluster.getDistrict())
                .city(cluster.getCity())
                .latitude(cluster.getLatitude())
                .longitude(cluster.getLongitude())
                .ownerId(cluster.getOwner() != null ? cluster.getOwner().getId() : null)
                .priceRangeDescription(minPrice != null && maxPrice != null
                        ? "Sân con từ " + minPrice + " đến " + maxPrice
                        : "")
                .imageUrls(imageUrls)
                .fields(fields)
                .build();
    }

    // Lấy tất cả clusters của owner hiện tại
    public List<ClusterResponse> listAllClusters() {
        User currentOwner = getCurrentOwner();
        return clusterRepository.findAll().stream()
                .filter(c -> c.getOwner().getId().equals(currentOwner.getId()))
                .map(this::mapClusterToResponse)
                .collect(Collectors.toList());
    }

    // Xem chi tiết cluster theo ID (chỉ owner xem được cluster của mình)
    public ClusterResponse getClusterDetail(Integer clusterId) {
        FieldCluster cluster = getClusterById(clusterId); // check quyền rồi
        return mapClusterToResponse(cluster);
    }
}
