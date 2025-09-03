package vti.group10.football_booking.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vti.group10.football_booking.model.FootballField;
import vti.group10.football_booking.repository.BookingRepository;
import vti.group10.football_booking.repository.FootballFieldRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FootballFieldService {
    
    @Autowired
    private FootballFieldRepository fieldRepository;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private FieldImageService fieldImageService;
    @Autowired
    private FieldScheduleService fieldScheduleService;
    @Autowired
    private BookingRepository bookingRepository;
    public List<FootballField> getAllFields() {
        return fieldRepository.findAll();
    }
    
    public Optional<FootballField> getFieldById(Integer id) {
        return fieldRepository.findById(id);
    }
    
    public List<FootballField> getFieldsByStatus(FootballField.Status status) {
        return fieldRepository.findByStatus(status);
    }
    
    public List<FootballField> searchFieldsByAddress(String address) {
        return fieldRepository.findByAddressContainingIgnoreCase(address);
    }
    
    public List<FootballField> searchFieldsByName(String name) {
        return fieldRepository.findByNameContainingIgnoreCase(name);
    }
    
    public FootballField createField(FootballField field) {
        if (field.getCreatedAt() == null) {
            field.setCreatedAt(LocalDateTime.now());
        }
        return fieldRepository.save(field);
    }
    
    public FootballField updateField(Integer id, FootballField fieldDetails) {
        FootballField field = fieldRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Field not found"));
        
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
    @Transactional
    public void deleteField(Integer fieldId) {
        List<Integer> bookingIds = bookingRepository.findByFieldId(fieldId)
                .stream()
                .map(b -> b.getId())
                .toList();
        for (Integer bookingId : bookingIds) {
            paymentService.deletePaymentsByBookingId(bookingId);
        }
        // 1. Xóa tất cả Booking của Field
        bookingService.deleteBookingsByFieldId(fieldId);
        fieldImageService.deleteFieldImagesByFieldId(fieldId);
        fieldScheduleService.deleteFieldSchedulesByFieldId(fieldId);
        // 2. Xóa Field
        fieldRepository.deleteById(fieldId);
    }

}