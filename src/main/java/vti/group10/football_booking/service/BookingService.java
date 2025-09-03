package vti.group10.football_booking.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vti.group10.football_booking.model.Booking;
import vti.group10.football_booking.repository.BookingRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {
    
    @Autowired
    private BookingRepository bookingRepository;
    
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
    
    public Optional<Booking> getBookingById(Integer id) {
        return bookingRepository.findById(id);
    }
    
    public List<Booking> getBookingsByUserId(Integer userId) {
        return bookingRepository.findByUserId(userId);
    }
    
    public List<Booking> getBookingsByFieldId(Integer fieldId) {
        return bookingRepository.findByFieldId(fieldId);
    }
    
    public List<Booking> getBookingsByStatus(Booking.Status status) {
        return bookingRepository.findByStatus(status);
    }
    
    public List<Booking> getBookingsByDate(LocalDate date) {
        return bookingRepository.findByBookingDate(date);
    }
    
    public Booking createBooking(Booking booking) {
        if (booking.getCreatedAt() == null) {
            booking.setCreatedAt(LocalDateTime.now());
        }
        return bookingRepository.save(booking);
    }
    
    public Booking updateBooking(Integer id, Booking bookingDetails) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        booking.setBookingDate(bookingDetails.getBookingDate());
        booking.setStartTime(bookingDetails.getStartTime());
        booking.setEndTime(bookingDetails.getEndTime());
        booking.setTotalPrice(bookingDetails.getTotalPrice());
        booking.setStatus(bookingDetails.getStatus());
        booking.setUserId(bookingDetails.getUserId());
        booking.setFieldId(bookingDetails.getFieldId());
        
        return bookingRepository.save(booking);
    }
    
    public void deleteBooking(Integer id) {
        bookingRepository.deleteById(id);
    }
    @Transactional
    public void deleteBookingsByFieldId(Integer fieldId) {
        bookingRepository.deleteByFieldId(fieldId);
    }
    @Transactional
    public void deleteBookingsByUserId(Integer userId) {
        bookingRepository.deleteByUserId(userId);
    }
}