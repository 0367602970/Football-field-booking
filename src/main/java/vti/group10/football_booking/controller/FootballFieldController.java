package vti.group10.football_booking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vti.group10.football_booking.model.FootballField;
import vti.group10.football_booking.repository.FootballFieldRepository;
import vti.group10.football_booking.service.FootballFieldService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fields")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class FootballFieldController {
    
    @Autowired
    private FootballFieldRepository fieldRepository;
    @Autowired
    private FootballFieldService fieldService;
    @GetMapping
    public List<FootballField> getAllFields() {
        return fieldRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public FootballField getFieldById(@PathVariable Integer id) {
        return fieldRepository.findById(id).orElse(null);
    }

    // Lấy danh sách sân theo owner_id
    @GetMapping("/owner/{ownerId}")
    public List<FootballField> getFieldsByOwnerId(@PathVariable Integer ownerId) {
        return fieldRepository.findByOwnerId(ownerId);
    }

    @PostMapping
    public FootballField createField(@RequestBody FootballField field) {
        field.setCreatedAt(LocalDateTime.now());
        return fieldRepository.save(field);
    }
    
    @PutMapping("/{id}")
    public FootballField updateField(@PathVariable Integer id, @RequestBody FootballField fieldDetails) {
        FootballField field = fieldRepository.findById(id).orElseThrow();
        field.setName(fieldDetails.getName());
        field.setAddress(fieldDetails.getAddress());
        field.setCity(fieldDetails.getCity());
        field.setDistrict(fieldDetails.getDistrict());
        field.setPricePerHour(fieldDetails.getPricePerHour());
        field.setDescription(fieldDetails.getDescription());
        field.setStatus(fieldDetails.getStatus());
        field.setOwnerId(fieldDetails.getOwnerId());
        field.setLatitude(fieldDetails.getLatitude());
        field.setLongitude(fieldDetails.getLongitude());
        return fieldRepository.save(field);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteField(@PathVariable("id") Integer fieldId) {
        fieldService.deleteField(fieldId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Field and its bookings deleted successfully.");
        return ResponseEntity.ok(response);
    }
}