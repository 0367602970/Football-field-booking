package vti.group10.football_booking.service.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vti.group10.football_booking.dto.response.FootballFieldResponse;
import vti.group10.football_booking.model.FootballField;
import vti.group10.football_booking.model.FieldImage;
import vti.group10.football_booking.dto.response.FootballFieldDetailResponse;
import vti.group10.football_booking.repository.FootballFieldRepository;
import vti.group10.football_booking.service.geo.GeocodingService;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Objects;
import java.util.Comparator;
@Service
@RequiredArgsConstructor
public class FootballFieldService {

    private final FootballFieldRepository repository;
    private final GeocodingService geocodingService;
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
                .imageUrls(limitImages(field.getImages(), 8))
                .latitude(field.getLatitude())
                .longitude(field.getLongitude())
                .build();
    }

    // 3. Tìm kiếm sân theo keyword (name hoặc location)
    public Page<FootballFieldResponse> searchFields(String keyword, Pageable pageable) {
        return repository.findByNameContainingIgnoreCaseOrAddressContainingIgnoreCaseOrDistrictContainingIgnoreCaseOrCityContainingIgnoreCase(
                        keyword, keyword, keyword, keyword, pageable)
                .map(this::mapToResponse);
    }

    // 4. Lọc sân theo city, district, priceRange (có phân trang)
    public Page<FootballFieldResponse> filterFields(String city, String district,
                                                    Double minPrice, Double maxPrice,
                                                    Pageable pageable) {
        // City/District có thể truyền null — repository xử lý
        return repository.filterFootballFields(city, district, minPrice, maxPrice, pageable)
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

    // helper: giới hạn số ảnh tối đa
    private List<String> limitImages(List<FieldImage> images, int limit) {
        return images.stream()
                .limit(limit)
                .map(FieldImage::getImageUrl)
                .collect(Collectors.toList());
    }


    private static final int EARTH_RADIUS_KM = 6371;

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return EARTH_RADIUS_KM * c;
    }

    /** Tự động geocode nếu thiếu toạ độ, rồi lưu lại */
    private void ensureLatLng(FootballField f) {
        if (f.getLatitude() != null && f.getLongitude() != null) return;
        String full = String.join(", ",
                Objects.toString(f.getAddress(), ""),
                Objects.toString(f.getDistrict(), ""),
                Objects.toString(f.getCity(), ""));
        geocodingService.geocode(full).ifPresent(latLng -> {
            f.setLatitude(latLng.lat());
            f.setLongitude(latLng.lng());
            repository.save(f);
        });
    }

    /** API logic: tìm sân trong bán kính radiusKm quanh (userLat, userLng) */
    public List<NearbyFieldResponse> findNearbyFields(double userLat, double userLng, double radiusKm) {
        return repository.findAll().stream()
                .peek(this::ensureLatLng) // geocode lazy nếu còn thiếu
                .filter(f -> f.getLatitude() != null && f.getLongitude() != null)
                .map(f -> {
                    double d = haversine(userLat, userLng, f.getLatitude(), f.getLongitude());
                    if (d <= radiusKm) {
                        return NearbyFieldResponse.of(f, round2(d));
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(NearbyFieldResponse::getDistanceKm))
                .collect(Collectors.toList());
    }

    private double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    // DTO gọn cho nearby (không đụng DTO cũ)
    @lombok.Value
    @lombok.Builder
    public static class NearbyFieldResponse {
        Integer id;
        String name;
        String address;
        String district;
        String city;
        Double pricePerHour;
        String status;
        Double latitude;
        Double longitude;
        Double distanceKm;
        List<String> imageUrls;

        public static NearbyFieldResponse of(FootballField f, double distanceKm) {
            return NearbyFieldResponse.builder()
                    .id(f.getId())
                    .name(f.getName())
                    .address(f.getAddress())
                    .district(f.getDistrict())
                    .city(f.getCity())
                    .pricePerHour(f.getPricePerHour())
                    .status(f.getStatus().name())
                    .latitude(f.getLatitude())
                    .longitude(f.getLongitude())
                    .distanceKm(distanceKm)
                    .imageUrls(f.getImages().stream().map(FieldImage::getImageUrl).collect(Collectors.toList()))
                    .build();
        }
    }
}
