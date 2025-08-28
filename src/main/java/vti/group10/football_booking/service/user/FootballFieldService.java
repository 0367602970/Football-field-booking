package vti.group10.football_booking.service.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vti.group10.football_booking.dto.response.FootballFieldResponse;
import vti.group10.football_booking.model.FootballField;
import vti.group10.football_booking.repository.FootballFieldRepository;
import vti.group10.football_booking.model.FieldImage;
import vti.group10.football_booking.dto.response.FootballFieldDetailResponse;
import lombok.RequiredArgsConstructor;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FootballFieldService {

    private final FootballFieldRepository repository;

    public Page<FootballFieldResponse> getAllFields(Pageable pageable) {
        return repository.findAll(pageable)
                .map(this::mapToResponse);
    }

    private FootballFieldResponse mapToResponse(FootballField field) {
        return FootballFieldResponse.builder()
                .id(field.getId())
                .name(field.getName())
                .location(field.getLocation())
                .pricePerHour(field.getPricePerHour())
                .description(field.getDescription())
                .status(field.getStatus().name())
                .imageUrls(field.getImages().stream()
                        .map(FieldImage::getImageUrl)   // cần import FieldImage
                        .collect(Collectors.toList()))
                .build();
    }
    // Lấy chi tiết sân theo id
    public FootballFieldDetailResponse getFieldById(Integer id) {
        FootballField field = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sân bóng không tồn tại với id = " + id));

        return FootballFieldDetailResponse.builder()
                .id(field.getId())
                .name(field.getName())
                .location(field.getLocation())
                .pricePerHour(field.getPricePerHour())
                .description(field.getDescription())
                .status(field.getStatus().name())
                .ownerName(field.getOwner().getFullName())
                .imageUrls(field.getImages().stream()
                        .limit(8) // chỉ lấy 8 ảnh
                        .map(img -> img.getImageUrl())
                        .collect(Collectors.toList()))
                .build();
    }
}