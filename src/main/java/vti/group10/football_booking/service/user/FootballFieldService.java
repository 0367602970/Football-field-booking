package vti.group10.football_booking.service.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vti.group10.football_booking.dto.response.FootballFieldResponse;
import vti.group10.football_booking.model.FootballField;
import vti.group10.football_booking.model.FieldImage;
import vti.group10.football_booking.dto.response.FootballFieldDetailResponse;
import vti.group10.football_booking.repository.FootballFieldRepository;
import lombok.RequiredArgsConstructor;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FootballFieldService {

    private final FootballFieldRepository repository;

    // 1. Lấy danh sách tất cả sân (có phân trang)
    public Page<FootballFieldResponse> getAllFields(Pageable pageable) {
        return repository.findAll(pageable)
                .map(this::mapToResponse);
    }

    // 2. Lấy chi tiết sân theo id
    public FootballFieldDetailResponse getFieldById(Integer id) {
        FootballField field = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sân bóng không tồn tại với id = " + id));

        return FootballFieldDetailResponse.builder()
                .id(field.getId())
                .name(field.getName())
                .address(field.getAddress())
                .district(field.getDistrict())
                .city(field.getCity())
                .pricePerHour(field.getPricePerHour())
                .description(field.getDescription())
                .status(field.getStatus().name())
                .ownerName(field.getOwner().getFullName())
                .imageUrls(field.getImages().stream()
                        .limit(8) // chỉ lấy tối đa 8 ảnh
                        .map(FieldImage::getImageUrl)
                        .collect(Collectors.toList()))
                .build();
    }

    // 3. Tìm kiếm sân theo keyword (name hoặc location)
    public Page<FootballFieldResponse> searchFields(String keyword, Pageable pageable) {
        return repository.findByNameContainingIgnoreCaseOrAddressContainingIgnoreCaseOrDistrictContainingIgnoreCaseOrCityContainingIgnoreCase(keyword, keyword, keyword, keyword, pageable)
                .map(this::mapToResponse);
    }

    // Hàm map sang DTO response
    private FootballFieldResponse mapToResponse(FootballField field) {
        return FootballFieldResponse.builder()
                .id(field.getId())
                .name(field.getName())
                .address(field.getAddress())
                .district(field.getDistrict())
                .city(field.getCity())
                .pricePerHour(field.getPricePerHour())
                .status(field.getStatus().name())
                .imageUrls(field.getImages().stream()
                        .map(FieldImage::getImageUrl)
                        .collect(Collectors.toList()))
                .build();
    }
}
