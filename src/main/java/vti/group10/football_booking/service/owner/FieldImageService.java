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
import vti.group10.football_booking.model.FootballField;
import vti.group10.football_booking.repository.FieldImageRepository;
import vti.group10.football_booking.repository.FootballFieldRepository;

@Service
@RequiredArgsConstructor
public class FieldImageService  {

    private final FootballFieldRepository fieldRepo;
    private final FieldImageRepository imageRepo;

    @Value("${app.upload.dir:uploads}") // lấy từ application.properties, mặc định "uploads"
    private String uploadDir;

    public FieldImage addImage(Long fieldId, MultipartFile file) throws IOException {
        FootballField field = fieldRepo.findById(fieldId)
                .orElseThrow(() -> new RuntimeException("Field not found"));

        // Thư mục lưu ảnh: {projectDir}/uploads/fields/{fieldId}
        String projectDir = System.getProperty("user.dir"); // thư mục gốc project
        Path uploadPath = Paths.get(projectDir, uploadDir, "fields", String.valueOf(fieldId));

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Tạo tên file duy nhất
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);

        // Lưu file vào disk
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Đường dẫn để client truy cập
        String imageUrl = "/uploads/fields/" + fieldId + "/" + fileName;

        // Lưu DB
        FieldImage image = FieldImage.builder()
                .imageUrl(imageUrl)
                .field(field)
                .build();

        return fieldRepo.save(image);
    }

    public List<FieldImage> getImages(Long fieldId) {
        return imageRepo.findByFieldId(fieldId);
    }

    public void deleteImage(Long imageId) {
        imageRepo.deleteById(imageId);
    }
}
