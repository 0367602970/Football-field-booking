package vti.group10.football_booking.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vti.group10.football_booking.dto.response.ClusterResponse;
import vti.group10.football_booking.service.owner.OwnerFieldClusterService;

import java.util.List;

@RestController
@RequestMapping("/admin/clusters")
@RequiredArgsConstructor
public class AdminFieldClusterController {
    private final OwnerFieldClusterService clusterService;
    @GetMapping
    public ResponseEntity<List<ClusterResponse>> listAllClusters() {
        List<ClusterResponse> clusters = clusterService.listAllClustersPublic();
        return ResponseEntity.ok(clusters);
    }
}
