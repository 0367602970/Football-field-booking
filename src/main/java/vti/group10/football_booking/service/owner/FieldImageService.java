package vti.group10.football_booking.service.owner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import vti.group10.football_booking.model.FieldImage;
import vti.group10.football_booking.repository.FieldImageRepository;
import vti.group10.football_booking.repository.FieldClusterRepository;
import vti.group10.football_booking.model.FieldCluster;


@Service
@RequiredArgsConstructor
public class FieldImageService {

    private final FieldImageRepository imageRepo;
    private final FieldClusterRepository clusterRepo; // cần để lấy cluster

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    public FieldImage addImage(Integer clusterId, MultipartFile file) throws IOException {
        // Lấy cluster từ DB
        FieldCluster cluster = clusterRepo.findById(clusterId)
                .orElseThrow(() -> new RuntimeException("Cluster not found"));

        // Thư mục lưu ảnh: {projectDir}/uploads/clusters/{clusterId}
        String projectDir = System.getProperty("user.dir");
        Path uploadPath = Paths.get(projectDir, uploadDir, "clusters", String.valueOf(clusterId));
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Tạo tên file duy nhất
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);

        // Lưu file
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // URL để client truy cập
        String imageUrl = "/uploads/clusters/" + clusterId + "/" + fileName;

        // Lưu vào DB
        FieldImage image = FieldImage.builder()
                .cluster(cluster)
                .imageUrl(imageUrl)
                .build();

        return imageRepo.save(image);
    }

    public List<FieldImage> getImages(Integer clusterId) {
        return imageRepo.findByClusterId(clusterId);
    }

    public void deleteImage(Integer imageId) {
        imageRepo.deleteById(imageId);
    }
}
