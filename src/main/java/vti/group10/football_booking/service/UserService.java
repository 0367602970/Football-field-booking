package vti.group10.football_booking.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vti.group10.football_booking.model.User;
import vti.group10.football_booking.repository.BookingRepository;
import vti.group10.football_booking.repository.FootballFieldRepository;
import vti.group10.football_booking.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
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
    @Autowired
    private NotificationService notificationService;
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public Optional<User> getUserById(Integer id) {
        return userRepository.findById(id);
    }
    
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public User handleCreateUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại");   
        }
        return userRepository.save(user);
    }
    
    public User updateUser(Integer id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setFullName(userDetails.getFullName());
        user.setEmail(userDetails.getEmail());
        user.setPhone(userDetails.getPhone());
        user.setRole(userDetails.getRole());
        
        return userRepository.save(user);
    }
    @Transactional
    public void deleteUser(Integer userId) {
        List<Integer> userBookingIds = bookingRepository.findByUserId(userId)
                .stream()
                .map(b -> b.getId())
                .toList();

        for (Integer bookingId : userBookingIds) {
            paymentService.deletePaymentsByBookingId(bookingId);
        }

        bookingService.deleteBookingsByUserId(userId);
        // 1. Lấy tất cả Field của user
        List<Integer> fieldIds = fieldRepository.findByOwnerId(userId)
                .stream()
                .map(f -> f.getId())
                .toList();

        // 2. Xóa từng Field như deleteField
        for (Integer fieldId : fieldIds) {
            List<Integer> bookingIds = bookingRepository.findByFieldId(fieldId)
                    .stream()
                    .map(b -> b.getId())
                    .toList();

            // Xóa payment của từng booking
            for (Integer bookingId : bookingIds) {
                paymentService.deletePaymentsByBookingId(bookingId);
            }

            // Xóa booking
            bookingService.deleteBookingsByFieldId(fieldId);

            // Xóa field images
            fieldImageService.deleteFieldImagesByFieldId(fieldId);

            // Xóa field schedules
            fieldScheduleService.deleteFieldSchedulesByFieldId(fieldId);

            // Xóa field
            fieldRepository.deleteById(fieldId);
        }

        // 3. Xóa notifications của user
        notificationService.deleteNotificationsByUserId(userId);

        // 4. Cuối cùng xóa user
        userRepository.deleteById(userId);
    }

}