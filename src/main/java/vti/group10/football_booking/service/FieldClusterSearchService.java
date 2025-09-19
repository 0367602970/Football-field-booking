package vti.group10.football_booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vti.group10.football_booking.model.FieldCluster;
import vti.group10.football_booking.repository.FieldClusterRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FieldClusterSearchService {

    private final FieldClusterRepository clusterRepository;

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
}
