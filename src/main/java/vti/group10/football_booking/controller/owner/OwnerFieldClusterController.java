package vti.group10.football_booking.controller.owner;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vti.group10.football_booking.dto.request.FieldClusterRequest;
import vti.group10.football_booking.dto.response.ClusterResponse;
import vti.group10.football_booking.model.FieldCluster;
import vti.group10.football_booking.service.owner.OwnerFieldClusterService;

import java.util.List;

@RestController
@RequestMapping("/owner/clusters")
@RequiredArgsConstructor
public class OwnerFieldClusterController {

    private final OwnerFieldClusterService clusterService;

    // Tạo cụm sân (kèm ảnh & sân con)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ClusterResponse> createCluster(
            @RequestPart("cluster") FieldClusterRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {

        FieldCluster cluster = clusterService.createCluster(request, images);
        return ResponseEntity.ok(clusterService.mapClusterToResponse(cluster));
    }


    // Lấy danh sách cluster của owner hiện tại
    @GetMapping
    public ResponseEntity<List<ClusterResponse>> listClusters() {
        System.out.println("Controller nhận request owner/clusters" );
        return ResponseEntity.ok(clusterService.listAllClusters());
    }

    // Xem chi tiết cluster
    @GetMapping("/{clusterId}")
    public ResponseEntity<ClusterResponse> getClusterDetail(@PathVariable int clusterId) {
        return ResponseEntity.ok(clusterService.getClusterDetail(clusterId));
    }

    // Cập nhật cluster
    @PutMapping("/{clusterId}")
    public ResponseEntity<ClusterResponse> updateCluster(
            @PathVariable int clusterId,
            @RequestPart("cluster") FieldClusterRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {

        FieldCluster cluster = clusterService.updateCluster(clusterId, request, images);
        return ResponseEntity.ok(clusterService.mapClusterToResponse(cluster));
    }


    // Thêm ảnh vào cluster
    @PostMapping("/{clusterId}/images")
    public ResponseEntity<ClusterResponse> addImage(@PathVariable int clusterId,
                                                    @RequestParam String imageUrl) {
        FieldCluster cluster = clusterService.addImageToCluster(clusterId, imageUrl);
        return ResponseEntity.ok(clusterService.mapClusterToResponse(cluster));
    }

    // Xóa cluster
    @DeleteMapping("/{clusterId}")
    public ResponseEntity<Void> deleteCluster(@PathVariable int clusterId) {
        clusterService.deleteCluster(clusterId);
        return ResponseEntity.noContent().build();
    }
}
