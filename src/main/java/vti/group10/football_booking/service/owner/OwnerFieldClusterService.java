package vti.group10.football_booking.service.owner;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vti.group10.football_booking.dto.request.FieldClusterRequest;
import vti.group10.football_booking.dto.response.ClusterResponse;
import vti.group10.football_booking.dto.response.FieldResponse;
import vti.group10.football_booking.model.FieldCluster;
import vti.group10.football_booking.model.FieldImage;
import vti.group10.football_booking.model.FootballField;
import vti.group10.football_booking.model.User;
import vti.group10.football_booking.repository.FieldClusterRepository;
import vti.group10.football_booking.repository.UserRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OwnerFieldClusterService {

    private final FieldClusterRepository clusterRepository;
    private final UserRepository userRepository;
    @Value("${app.upload.dir}")
    private String uploadDir;
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("🔎 Current logged in user: " + email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Owner not found with email: " + email));
    }
    public List<ClusterResponse> listAllClustersPublic() {
        List<FieldCluster> clusters = clusterRepository.findAll(); // Lấy tất cả cluster
        return clusters.stream()
                .filter(c -> c.getVisible() == FieldCluster.YesNo.YES)
                .map(this::mapClusterToResponse)
                .collect(Collectors.toList());
    }
    // Lấy cluster theo ID (và check quyền sở hữu)
    public FieldCluster getClusterById(int clusterId) {
        System.out.println("hàm getClusterById đã chạy!! ");
        User currentUser = getCurrentUser();
        FieldCluster cluster = clusterRepository.findById(clusterId)
                .orElseThrow(() -> new RuntimeException("Cluster not found with ID: " + clusterId));
        Set<FootballField> visibleFields = cluster.getFields().stream()
                .filter(f -> f.getVisible() == FootballField.YesNo.YES)
                .collect(Collectors.toSet());

// nếu muốn chỉ giữ visibleFields trong cluster:
        // Nếu không phải ADMIN và không phải owner của cluster → Access denied
        if (currentUser.getRole() != User.Role.ADMIN &&
                !cluster.getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied");
        }
        return cluster;
    }

    // Tạo cluster mới kèm ảnh và sân con
    public FieldCluster createCluster(FieldClusterRequest request, List<MultipartFile> images) {
        User owner = getCurrentUser();

        FieldCluster cluster = FieldCluster.builder()
                .name(request.getName())
                .address(request.getAddress())
                .district(request.getDistrict())
                .city(request.getCity())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .owner(owner)
                .visible(FieldCluster.YesNo.YES)
                .images(new HashSet<>())
                .fields(new HashSet<>())
                .build();

        cluster = clusterRepository.save(cluster); // cần save trước để có clusterId


        // ✅ Lưu ảnh từ file upload
        if (images != null) {
            for (MultipartFile file : images) {
                if (!file.isEmpty()) {
                    String uploadedUrl = saveFile(file, cluster.getId());
                    cluster.getImages().add(FieldImage.builder()
                            .imageUrl(uploadedUrl)
                            .cluster(cluster)
                            .build());
                }
            }
        }


        return clusterRepository.save(cluster);
    }


//    public FieldCluster createCluster(FieldClusterRequest request) {
//        User owner = getCurrentOwner();
//
//        FieldCluster cluster = FieldCluster.builder()
//                .name(request.getName())
//                .address(request.getAddress())
//                .district(request.getDistrict())
//                .city(request.getCity())
//                .latitude(request.getLatitude())
//                .longitude(request.getLongitude())
//                .owner(owner)
//                .images(new HashSet<>())  // tránh null
//                .fields(new HashSet<>())  // tránh null
//                .build();
//
//        // Thêm hình ảnh (nếu có)
//        if (request.getImages() != null) {
//            request.getImages().forEach(imgReq ->
//                    cluster.getImages().add(FieldImage.builder()
//                            .imageUrl(imgReq.getImageUrl())
//                            .cluster(cluster)
//                            .build())
//            );
//        }
//
//        // Thêm sân con (nếu có)
//        if (request.getFields() != null) {
//            request.getFields().forEach(fReq ->
//                    cluster.getFields().add(FootballField.builder()
//                            .name(fReq.getName())
//                            .description(fReq.getDescription())
//                            .pricePerHour(fReq.getPricePerHour())
//                            .status(FootballField.Status.valueOf(fReq.getStatus()))
//                            .cluster(cluster)
//                            .build())
//            );
//        }
//
//        return clusterRepository.save(cluster);
//    }

    // Cập nhật cluster (chỉ owner của cluster được update)
    public FieldCluster updateCluster(int clusterId, FieldClusterRequest request, List<MultipartFile> images) {
        FieldCluster cluster = getClusterById(clusterId); // đã check quyền

        // Cập nhật thông tin cơ bản
        cluster.setName(request.getName());
        cluster.setAddress(request.getAddress());
        cluster.setDistrict(request.getDistrict());
        cluster.setCity(request.getCity());
        cluster.setLatitude(request.getLatitude());
        cluster.setLongitude(request.getLongitude());

        // Xử lý ảnh upload mới
        if (images != null && !images.isEmpty()) {
            for (MultipartFile file : images) {
                String url = saveFile(file, cluster.getId()); // dùng hàm saveFile
                cluster.getImages().add(FieldImage.builder()
                        .imageUrl(url)
                        .cluster(cluster)
                        .build());
            }
        }

        // Nếu muốn: xử lý fields (sân con) tương tự createCluster

        return clusterRepository.save(cluster);
    }


    // Xóa cluster (chỉ owner được xóa)
    public void deleteCluster(int clusterId) {
        FieldCluster cluster = getClusterById(clusterId); // check quyền rồi
        cluster.setVisible(FieldCluster.YesNo.NO);
        clusterRepository.save(cluster);
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
        User currentUser = getCurrentUser();
        return clusterRepository.findAll().stream()
                .filter(c -> c.getOwner().getId().equals(currentUser.getId()))
                .filter(c -> c.getVisible() == FieldCluster.YesNo.YES)
                .map(this::mapClusterToResponse)
                .collect(Collectors.toList());
    }

    // Xem chi tiết cluster theo ID (chỉ owner xem được cluster của mình)
    public ClusterResponse getClusterDetail(Integer clusterId) {
        FieldCluster cluster = getClusterById(clusterId); // check quyền rồi

        // Lọc field visible = YES
        Set<FootballField> visibleFields = cluster.getFields().stream()
                .filter(f -> f.getVisible() == FootballField.YesNo.YES)
                .collect(Collectors.toSet());

        // Tạo một bản clone của cluster để map (không ảnh hưởng entity gốc)
        FieldCluster clusterForResponse = FieldCluster.builder()
                .id(cluster.getId())
                .name(cluster.getName())
                .address(cluster.getAddress())
                .district(cluster.getDistrict())
                .city(cluster.getCity())
                .latitude(cluster.getLatitude())
                .longitude(cluster.getLongitude())
                .createdAt(cluster.getCreatedAt())
                .owner(cluster.getOwner())
                .fields(visibleFields)
                .images(cluster.getImages())
                .build();

        return mapClusterToResponse(clusterForResponse);
    }

    private String saveFile(MultipartFile file, int clusterId) {
        try {
            // Thư mục riêng cho cluster
            Path clusterDir = Paths.get(uploadDir, "fields", String.valueOf(clusterId));
            if (!Files.exists(clusterDir)) {
                Files.createDirectories(clusterDir);
            }

            // Đặt tên file unique
            String originalName = file.getOriginalFilename();
            String ext = "";
            if (originalName != null && originalName.contains(".")) {
                ext = originalName.substring(originalName.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID() + ext;

            // Lưu file vật lý
            Path filePath = clusterDir.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Trả về URL public (dùng để lưu DB)
            return "/uploads/fields/" + clusterId + "/" + fileName;

        } catch (IOException e) {
            throw new RuntimeException("Upload file thất bại: " + file.getOriginalFilename(), e);
        }
    }
}
