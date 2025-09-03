    package vti.group10.football_booking.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vti.group10.football_booking.model.FieldImage;
import vti.group10.football_booking.repository.FieldImageRepository;

import java.util.List;
import java.util.Optional;

@Service
public class FieldImageService {
    
    @Autowired
    private FieldImageRepository fieldImageRepository;
    
    public List<FieldImage> getAllFieldImages() {
        return fieldImageRepository.findAll();
    }
    
    public Optional<FieldImage> getFieldImageById(Integer id) {
        return fieldImageRepository.findById(id);
    }
    
    public List<FieldImage> getImagesByFieldId(Integer fieldId) {
        return fieldImageRepository.findByFieldId(fieldId);
    }
    
    public FieldImage createFieldImage(FieldImage fieldImage) {
        return fieldImageRepository.save(fieldImage);
    }
    
    public FieldImage updateFieldImage(Integer id, FieldImage fieldImageDetails) {
        FieldImage fieldImage = fieldImageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Field image not found"));
        
        fieldImage.setImageUrl(fieldImageDetails.getImageUrl());
        
        return fieldImageRepository.save(fieldImage);
    }

    @Transactional
    public void deleteFieldImagesByFieldId(Integer fieldId) {
        fieldImageRepository.deleteByFieldId(fieldId);
    }
}