package vti.group10.football_booking.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vti.group10.football_booking.dto.response.ClusterResponse;
import vti.group10.football_booking.dto.response.PageResult;
import vti.group10.football_booking.service.user.UserFieldClusterService;

import java.util.List;

@RestController
@RequestMapping("/clusters")
@RequiredArgsConstructor
public class UserFieldClusterController {

    private final UserFieldClusterService clusterService;

    @GetMapping("/all")
    public ResponseEntity<PageResult<ClusterResponse>> getAllClusters(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PageResult<ClusterResponse> result = clusterService.listClustersPaginated(page, size);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<List<ClusterResponse>> listClusters() {
        return ResponseEntity.ok(clusterService.listClusters());
    }

    @GetMapping("/{clusterId}")
    public ResponseEntity<ClusterResponse> getClusterDetail(@PathVariable int clusterId) {
        ClusterResponse res = clusterService.getClusterDetail(clusterId);
        if (res == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(res);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ClusterResponse>> searchClusters(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(clusterService.searchClusters(keyword, page, size));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<ClusterResponse>> filterClusters(
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String city) {
        return ResponseEntity.ok(clusterService.filterClusters(address, district, city));
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<ClusterResponse>> getNearbyClusters(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "5") double radiusKm) {

        List<ClusterResponse> clusters = clusterService.getNearbyClusters(lat, lng, radiusKm);
        return ResponseEntity.ok(clusters);
    }
}
