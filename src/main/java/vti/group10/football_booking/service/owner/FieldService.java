package vti.group10.football_booking.service.owner;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;
import vti.group10.football_booking.dto.request.FieldRequest;
import vti.group10.football_booking.dto.request.FieldUpdateRequest;
import vti.group10.football_booking.dto.response.FieldResponse;
import vti.group10.football_booking.model.FieldImage;
import vti.group10.football_booking.model.FootballField;
import vti.group10.football_booking.model.User;
import vti.group10.football_booking.repository.FootballFieldRepository;
import vti.group10.football_booking.service.CustomUserDetailsService;
import vti.group10.football_booking.service.MapService;

@Service
public class FieldService {
    private final FootballFieldRepository fieldRepo;
    @Autowired
    private MapService mapService;
    private final FileStorageService fileStorageService;
    public FieldService(FootballFieldRepository fieldRepo,
                        FileStorageService fileStorageService) {
        this.fieldRepo = fieldRepo;
        this.fileStorageService = fileStorageService;
    }

    public Page<FieldResponse> getAllFields(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<FootballField> fields = fieldRepo.findAll(pageable);
        return fields.map(this::toDto);
    }

    public FieldResponse getFieldById(Integer id) {
        FootballField field = fieldRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Field not found"));

        return FieldResponse.builder()
                .id(field.getId())
                .name(field.getName())
                .address(field.getAddress())
                .district(field.getDistrict())
                .city(field.getCity())
                .description(field.getDescription())
                .pricePerHour(field.getPricePerHour())
                .status(field.getStatus().name())
                .images(field.getImages() != null
                        ? field.getImages().stream()
                        .map(FieldImage::getImageUrl) // giả sử entity FieldImage có field url
                        .toList()
                        : List.of())
                .build();
    }


    public FieldResponse createField(FieldRequest req, User currentUser, List<MultipartFile> images) throws IOException {
        Double latitude = null;
        Double longitude = null;

        // Nếu request có latitude & longitude, ưu tiên dùng
        if (req.getLatitude() != null && req.getLongitude() != null) {
            latitude = req.getLatitude();
            longitude = req.getLongitude();
        } else {
            // Lấy tọa độ từ MapService
            try {
                Map<String, Double> coords = mapService.getCoordinates(
                        req.getAddress(),
                        req.getDistrict(),
                        req.getCity()
                );
                latitude = coords.get("lat");
                longitude = coords.get("lng");
            } catch (Exception e) {
                System.out.println("Không tìm thấy tọa độ: " + e.getMessage());
            }
        }

        FootballField field = FootballField.builder()
                .name(req.getName())
                .address(req.getAddress())
                .district(req.getDistrict())
                .city(req.getCity())
                .description(req.getDescription())
                .pricePerHour(req.getPricePerHour())
                .status(FootballField.Status.AVAILABLE)
                .createdAt(LocalDateTime.now())
                .owner(currentUser)
                .latitude(latitude)
                .longitude(longitude)
                .build();
        fieldRepo.save(field);
        // Lưu ảnh nếu có upload
        if (images != null) {
            for (MultipartFile file : images) {
                String url = fileStorageService.storeFile(file, field.getId()); // Lưu file, trả về URL
                FieldImage img = FieldImage.builder()
                        .imageUrl(url)
                        .field(field)
                        .build();
                field.addImage(img);
            }
            fieldRepo.save(field);
        }


        return toDto(field);
    }



//    public FieldResponse createField(FieldRequest req, User currentUser) {
//        Double latitude = null;
//        Double longitude = null;
//        System.out.println("Map request: " + req.getAddress() + ", " + req.getDistrict() + ", " + req.getCity());
//
//        // Lấy tọa độ từ MapService
//        try {
//            Map<String, Double> coords = mapService.getCoordinates(
//                    req.getAddress(),
//                    req.getDistrict(),
//                    req.getCity()
//            );
//            System.out.println("Coordinates: " + coords);
//            latitude = coords.get("lat");
//            longitude = coords.get("lng");
//        } catch (Exception e) {
//            // Nếu không tìm thấy, có thể log hoặc bỏ qua
//            System.out.println("Không tìm thấy tọa độ: " + e.getMessage());
//        }
//
//        FootballField field = FootballField.builder()
//                .name(req.getName())
//                .address(req.getAddress())
//                .district(req.getDistrict())
//                .city(req.getCity())
//                .description(req.getDescription())
//                .pricePerHour(req.getPricePerHour())
//                .status(FootballField.Status.AVAILABLE)
//                .createdAt(LocalDateTime.now())
//                .owner(currentUser)   // 👈 gán owner luôn
//                .latitude(latitude)
//                .longitude(longitude)
//                .build();
//
//        // nếu có ảnh truyền kèm theo
//        if (req.getImages() != null) {
//            for (String url : req.getImages()) {
//                FieldImage img = FieldImage.builder()
//                        .imageUrl(url)
//                        .build();
//                field.addImage(img);
//            }
//        }
//
//        fieldRepo.save(field);
//        return toDto(field);
//    }

    private FieldResponse toDto(FootballField field) {
        return FieldResponse.builder()
                .id(field.getId())
                .name(field.getName())
                .address(field.getAddress())
                .district(field.getDistrict())
                .city(field.getCity())
                .description(field.getDescription())
                .pricePerHour(field.getPricePerHour())
                .status(field.getStatus().name())
                .latitude(field.getLatitude())
                .longitude(field.getLongitude())
                .images(field.getImages().stream()
                        .map(FieldImage::getImageUrl)
                        .toList())
                .build();
    }

    public FieldResponse updateField(Integer id, FieldUpdateRequest req) {
        FootballField field = fieldRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Field not found"));

        if (req.getName() != null)
            field.setName(req.getName());
        if (req.getAddress() != null)
            field.setAddress(req.getAddress());
        if (req.getDistrict() != null)
            field.setDistrict(req.getDistrict());
        if (req.getCity() != null)
            field.setCity(req.getCity());
        if (req.getDescription() != null)
            field.setDescription(req.getDescription());
        if (req.getPricePerHour() != null)
            field.setPricePerHour(req.getPricePerHour());
        if (req.getStatus() != null) {
            field.setStatus(FootballField.Status.valueOf(req.getStatus().toUpperCase()));
        }

        FootballField updated = fieldRepo.save(field);
        return toDto(updated);
    }

    public void deleteField(Integer id) {
        fieldRepo.deleteById(id);
    }
}
