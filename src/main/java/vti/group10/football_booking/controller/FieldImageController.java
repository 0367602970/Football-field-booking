package vti.group10.football_booking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vti.group10.football_booking.model.FieldImage;
import vti.group10.football_booking.repository.FieldImageRepository;

import java.util.List;

@RestController
@RequestMapping("/api/field-images")
@CrossOrigin(originPatterns = {"http://localhost:*", "https://localhost:*"})
public class FieldImageController {
    
    @Autowired
    private FieldImageRepository fieldImageRepository;
    
    @GetMapping
    public List<FieldImage> getAllFieldImages() {
        return fieldImageRepository.findAll();
    }
    
    @GetMapping("/field/{fieldId}")
    public List<FieldImage> getImagesByFieldId(@PathVariable Integer fieldId) {
        return fieldImageRepository.findByFieldId(fieldId);
    }
    
    @PostMapping
    public FieldImage createFieldImage(@RequestBody FieldImage fieldImage) {
        return fieldImageRepository.save(fieldImage);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFieldImage(@PathVariable Integer id) {
        fieldImageRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}