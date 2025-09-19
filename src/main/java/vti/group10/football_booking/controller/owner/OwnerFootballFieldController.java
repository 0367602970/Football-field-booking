package vti.group10.football_booking.controller.owner;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vti.group10.football_booking.dto.request.FieldRequest;
import vti.group10.football_booking.dto.response.FieldResponse;
import vti.group10.football_booking.model.FieldCluster;
import vti.group10.football_booking.model.FootballField;
import vti.group10.football_booking.service.owner.FootballFieldService;
import vti.group10.football_booking.service.owner.OwnerFieldClusterService;

import java.util.List;

@RestController
@RequestMapping("/owner/fields")
@RequiredArgsConstructor
public class OwnerFootballFieldController {

    private final FootballFieldService fieldService;
    private final OwnerFieldClusterService clusterService;

    @PostMapping("/{clusterId}")
    public ResponseEntity<FieldResponse> createField(
            @PathVariable int clusterId,
            @RequestBody FieldRequest request) {

        FieldCluster cluster = clusterService.getClusterById(clusterId);
        FootballField field = fieldService.createField(request, cluster);
        return ResponseEntity.ok(fieldService.mapFieldToResponse(field));
    }

    @PutMapping("/{fieldId}")
    public ResponseEntity<FieldResponse> updateField(
            @PathVariable int fieldId,
            @RequestBody FieldRequest request) {

        FootballField updated = fieldService.updateField(fieldId, request);
        return ResponseEntity.ok(fieldService.mapFieldToResponse(updated));
    }

    @DeleteMapping("/{fieldId}")
    public ResponseEntity<Void> deleteField(@PathVariable int fieldId) {
        fieldService.deleteField(fieldId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/cluster/{clusterId}")
    public ResponseEntity<List<FieldResponse>> listByCluster(@PathVariable int clusterId) {
        return ResponseEntity.ok(fieldService.listFieldsByCluster(clusterId));
    }

    // 🔥 Lọc sân theo priceRange (1,2,3)
    @GetMapping("/filter")
    public ResponseEntity<List<FieldResponse>> filterByPriceRange(
            @RequestParam(required = false) Integer priceRange,
            @RequestParam(required = false) String clusterName
    ) {
        Double minPrice = null;
        Double maxPrice = null;

        if (priceRange != null) {
            switch (priceRange) {
                case 1: // 1000 - 200000
                    minPrice = 1000.0;
                    maxPrice = 200000.0;
                    break;
                case 2: // 201000 - 400000
                    minPrice = 201000.0;
                    maxPrice = 400000.0;
                    break;
                case 3: // 401000 - 600000
                    minPrice = 401000.0;
                    maxPrice = 600000.0;
                    break;
            }
        }

        return ResponseEntity.ok(
                fieldService.filterFieldsByPriceAndCluster(minPrice, maxPrice, clusterName)
        );
    }
}
