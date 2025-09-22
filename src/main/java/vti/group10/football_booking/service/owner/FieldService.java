package vti.group10.football_booking.service.owner;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import vti.group10.football_booking.dto.request.FieldRequest;
import vti.group10.football_booking.dto.request.FieldUpdateRequest;
import vti.group10.football_booking.dto.response.FieldResponse;
import vti.group10.football_booking.model.FieldCluster;
import vti.group10.football_booking.model.FootballField;
import vti.group10.football_booking.model.User;
import vti.group10.football_booking.repository.FootballFieldRepository;

@Service
public class FieldService {
    private final FootballFieldRepository fieldRepo;

    @Autowired
    public FieldService(FootballFieldRepository fieldRepo) {
        this.fieldRepo = fieldRepo;
    }

    public Page<FieldResponse> getAllFields(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<FootballField> fields = fieldRepo.findAll(pageable);
        return fields.map(this::toDto);
    }

    public FieldResponse getFieldById(Integer id) {
        FootballField field = fieldRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Field not found"));

        return toDto(field);
    }

    public FieldResponse createField(FieldRequest req, FieldCluster cluster) {
        FootballField field = FootballField.builder()
                .name(req.getName())
                .description(req.getDescription())
                .pricePerHour(req.getPricePerHour())
                .status(FootballField.Status.AVAILABLE)
                .createdAt(LocalDateTime.now())
                .cluster(cluster)  // Liên kết sân con với cụm sân
                .build();

        fieldRepo.save(field);
        return toDto(field);
    }


    private FieldResponse toDto(FootballField field) {
        return FieldResponse.builder()
                .id(field.getId())
                .name(field.getName())
                .description(field.getDescription())
                .pricePerHour(field.getPricePerHour())
                .status(field.getStatus().name())
                .build();
    }

    public FieldResponse updateField(Integer id, FieldUpdateRequest req) {
        FootballField field = fieldRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Field not found"));

        if (req.getName() != null)
            field.setName(req.getName());
        if (req.getDescription() != null)
            field.setDescription(req.getDescription());
        if (req.getPricePerHour() != null)
            field.setPricePerHour(req.getPricePerHour());
        if (req.getStatus() != null) {
            field.setStatus(FootballField.Status.valueOf(req.getStatus().toUpperCase()));
        }

        return toDto(fieldRepo.save(field));
    }

    public void deleteField(Integer id) {
        fieldRepo.deleteById(id);
    }
}
