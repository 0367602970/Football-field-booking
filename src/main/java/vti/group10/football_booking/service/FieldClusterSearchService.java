package vti.group10.football_booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vti.group10.football_booking.model.FieldCluster;
import vti.group10.football_booking.model.FootballField;
import vti.group10.football_booking.repository.FieldClusterRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class FieldClusterSearchService {

    private final FieldClusterRepository clusterRepository;
    private final List<String> cities = List.of("Hà Nội", "Hồ Chí Minh");
    private final List<String> districtsHN = List.of("Ba Đình", "Hoàn Kiếm", "Tây Hồ", "Cầu Giấy", "Hà Đông");
    private final List<String> districtsHCM = List.of("Quận 1", "Quận 3", "Quận 5", "Quận 10");
    /**
     * Tìm clusters theo keyword từ chatbox
     */
    public List<FieldCluster> searchByKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return List.of();
        }

        // Lấy tất cả clusters và filter bằng Java stream
        String kw = keyword.toLowerCase();
        return clusterRepository.findAll().stream()
                .filter(c -> c.getName().toLowerCase().contains(kw) ||
                        c.getAddress().toLowerCase().contains(kw) ||
                        c.getDistrict().toLowerCase().contains(kw) ||
                        c.getCity().toLowerCase().contains(kw))
                .toList();
    }
    public List<FieldCluster> searchByKeywordSmart(String keyword) {
        if (keyword == null || keyword.isBlank()) return List.of();

        String kw = keyword.toLowerCase(Locale.ROOT);

        // Tách price nếu có (ví dụ: "200k" -> 200000)
        Integer priceFilter = parsePriceFromKeyword(kw);
        final Integer finalPriceFilter = priceFilter;
        // Lấy tất cả clusters
        List<FieldCluster> allClusters = clusterRepository.findAll();

        List<FieldCluster> filtered = new ArrayList<>();
        for (FieldCluster c : allClusters) {
            if (c.getVisible() != FieldCluster.YesNo.YES) continue;
            boolean matchesCity = c.getCity() != null && kw.contains(c.getCity().toLowerCase());
            boolean matchesDistrict = c.getDistrict() != null && kw.contains(c.getDistrict().toLowerCase());
            boolean matchesPrice = true;

            if (finalPriceFilter != null && c.getFields() != null && !c.getFields().isEmpty()) {
                int lower = finalPriceFilter - 50_000;
                int upper = finalPriceFilter + 50_000;

                matchesPrice = c.getFields().stream()
                        .filter(f -> f.getVisible() == FootballField.YesNo.YES)
                        .map(FootballField::getPricePerHour)
                        .anyMatch(p -> p != null && p >= lower && p <= upper);
            }

            if ((matchesCity || matchesDistrict) && matchesPrice) {
                filtered.add(c);
            }
        }

        return filtered;
    }
    private Integer parsePriceFromKeyword(String kw) {
        if (kw == null) return null;

        String normalized = kw.toLowerCase().replaceAll("\\s+", ""); // loại khoảng trắng
        String numberStr = normalized.replaceAll("[^0-9]", ""); // lấy số

        if (numberStr.isEmpty()) return null;

        int number = Integer.parseInt(numberStr);

        if (normalized.contains("k") || normalized.contains("nghìn") || normalized.contains("nghin") || normalized.contains("ngan") || normalized.contains("ngàn")) {
            return number * 1000;
        } else {
            return number;
        }
    }
}
