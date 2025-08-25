package vti.group10.football_booking.service.owner;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import vti.group10.football_booking.dto.request.FieldRequest;
import vti.group10.football_booking.dto.request.FieldUpdateRequest;
import vti.group10.football_booking.dto.response.FieldResponse;
import vti.group10.football_booking.model.FieldImage;
import vti.group10.football_booking.model.FootballField;
import vti.group10.football_booking.repository.FootballFieldRepository;

@Service
public class FieldService {
    private final FootballFieldRepository fieldRepo;

    public FieldService(FootballFieldRepository fieldRepo) {
        this.fieldRepo = fieldRepo;
    }

    public FieldResponse createField(FieldRequest req) {
        FootballField field = FootballField.builder()
                .name(req.getName())
                .location(req.getLocation())
                .description(req.getDescription())
                .pricePerHour(req.getPricePerHour())
                .status(FootballField.Status.AVAILABLE)
                .createdAt(LocalDateTime.now())
                .build();

        // nếu có ảnh truyền kèm theo
        if (req.getImages() != null) {
            for (String url : req.getImages()) {
                FieldImage img = FieldImage.builder()
                        .imageUrl(url)
                        .build();
                field.addImage(img); // dùng helper
            }
        }
        fieldRepo.save(field);

        return FieldResponse.builder()
                .id(field.getId())
                .name(field.getName())
                .location(field.getLocation())
                .description(field.getDescription())
                .pricePerHour(field.getPricePerHour())
                .status(field.getStatus().name())
                .images(field.getImages().stream().map(FieldImage::getImageUrl).toList())
                .build();
    }

    private FieldResponse toDto(FootballField field) {
        return FieldResponse.builder()
                .id(field.getId())
                .name(field.getName())
                .location(field.getLocation())
                .description(field.getDescription())
                .pricePerHour(field.getPricePerHour())
                .status(field.getStatus().name())
                .images(field.getImages().stream()
                        .map(FieldImage::getImageUrl)
                        .toList())
                .build();
    }

    public FieldResponse updateField(Long id, FieldUpdateRequest req) {
        FootballField field = fieldRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Field not found"));

        if (req.getName() != null) field.setName(req.getName());
        if (req.getLocation() != null) field.setLocation(req.getLocation());
        if (req.getDescription() != null) field.setDescription(req.getDescription());
        if (req.getPricePerHour() != null) field.setPricePerHour(req.getPricePerHour());
        if (req.getStatus() != null) {
            field.setStatus(FootballField.Status.valueOf(req.getStatus().toUpperCase()));
        }

        FootballField updated = fieldRepo.save(field);
        return toDto(updated);
    }

    public void deleteField(Long id) {
        fieldRepo.deleteById(id);
    }
}
