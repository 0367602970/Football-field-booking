package vti.group10.football_booking.service.owner;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.upload.dir}")
    private String uploadDir;
    private final String uploadDir2 = "E:/springboot/test2/uploads";
    private final String uploadRoot = "fields"; // con folder trong uploadDir

    public String storeFile(MultipartFile file, Integer fieldId) throws IOException {
        if (file.isEmpty()) throw new IOException("File rỗng");
        Path dir1 = Paths.get(uploadDir, uploadRoot, String.valueOf(fieldId)).toAbsolutePath().normalize();
        Path dir2 = Paths.get(uploadDir2, uploadRoot, String.valueOf(fieldId)).toAbsolutePath().normalize();

        if (!Files.exists(dir1)) Files.createDirectories(dir1);
        if (!Files.exists(dir2)) Files.createDirectories(dir2);
        // Tạo folder fieldId nếu chưa tồn tại

        // Tạo tên file duy nhất
        String ext = getExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID().toString() + (ext.isEmpty() ? "" : "." + ext);
        Path filePath1 = dir1.resolve(filename);
        Path filePath2 = dir2.resolve(filename);
        file.transferTo(filePath1.toFile());
        Files.copy(filePath1, filePath2);

        // URL trả về DB (tương ứng với static-locations)
        return "/uploads/fields/" + fieldId + "/" + filename;
    }

    private String getExtension(String name) {
        if (name == null) return "";
        int i = name.lastIndexOf('.');
        return i >= 0 ? name.substring(i + 1) : "";
    }
}
