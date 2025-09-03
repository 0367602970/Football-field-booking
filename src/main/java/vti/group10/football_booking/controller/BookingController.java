package vti.group10.football_booking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import vti.group10.football_booking.model.Booking;
import vti.group10.football_booking.model.Notification;
import vti.group10.football_booking.model.Payment;
import vti.group10.football_booking.repository.BookingRepository;
import vti.group10.football_booking.repository.PaymentRepository;
import vti.group10.football_booking.service.NotificationService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class BookingController {
    
    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @GetMapping
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public Booking getBookingById(@PathVariable Integer id) {
        return bookingRepository.findById(id).orElse(null);
    }

    @GetMapping("/owner/{ownerId}")
    public List<Booking> getBookingsByOwner(@PathVariable Integer ownerId) {
        // Giả sử Booking có fieldId, và FootballFieldRepository có thể tìm fields theo ownerId
        List<Integer> fieldIds = bookingRepository.findFieldsByOwnerId(ownerId);
        if (fieldIds.isEmpty()) {
            return List.of();
        }
        return bookingRepository.findByFieldIdIn(fieldIds);
    }

    @PostMapping
    public Booking createBooking(@RequestBody Booking booking) {
        booking.setCreatedAt(LocalDateTime.now());
        Booking savedBooking = bookingRepository.save(booking);
        
        // Tạo thông báo cho đặt sân mới
        try {
            Notification notification = new Notification();
            notification.setUserId(savedBooking.getUserId());
            notification.setMessage("Đặt sân mới đã được tạo. Mã đặt sân: #" + 
                String.format("%06d", savedBooking.getId()) + 
                ". Trạng thái: Chờ xác nhận.");
            notification.setStatus(Notification.Status.UNREAD);
            notification.setCreatedAt(LocalDateTime.now());
            notificationService.createNotification(notification);
        } catch (Exception e) {
            logger.warn("Failed to create notification for new booking: " + savedBooking.getId(), e);
        }
        
        return savedBooking;
    }
    
    @PutMapping("/{id}")
    public Booking updateBooking(@PathVariable Integer id, @RequestBody Booking bookingDetails) {
        Booking existingBooking = bookingRepository.findById(id).orElseThrow();
        String oldStatus = existingBooking.getStatus().toString();
        
        Booking booking = bookingRepository.findById(id).orElseThrow();
        booking.setBookingDate(bookingDetails.getBookingDate());
        booking.setStartTime(bookingDetails.getStartTime());
        booking.setEndTime(bookingDetails.getEndTime());
        booking.setTotalPrice(bookingDetails.getTotalPrice());
        booking.setStatus(bookingDetails.getStatus());
        booking.setUserId(bookingDetails.getUserId());
        booking.setFieldId(bookingDetails.getFieldId());
        
        Booking savedBooking = bookingRepository.save(booking);
        
        // Tự động tạo payment khi booking được CONFIRMED
        if (savedBooking.getStatus() == Booking.Status.CONFIRMED && !oldStatus.equals("CONFIRMED")) {
            try {
                // Kiểm tra xem đã có payment cho booking này chưa
                List<Payment> existingPayments = paymentRepository.findByBookingId(savedBooking.getId());
                if (existingPayments.isEmpty()) {
                    Payment payment = new Payment();
                    payment.setBookingId(savedBooking.getId());
                    payment.setAmount(savedBooking.getTotalPrice());
                    payment.setPaymentMethod(Payment.PaymentMethod.CASH); // Default method
                    payment.setPaymentDate(LocalDateTime.now());
                    payment.setStatus(Payment.Status.PENDING);
                    paymentRepository.save(payment);
                    
                    logger.info("Auto-created payment for confirmed booking ID: {}", savedBooking.getId());
                }
            } catch (Exception e) {
                logger.error("Failed to create payment for confirmed booking: " + savedBooking.getId(), e);
            }
        }
        
        // Tạo thông báo khi thay đổi trạng thái
        String newStatus = savedBooking.getStatus().toString();
        if (!oldStatus.equals(newStatus)) {
            try {
                String message = "";
                switch (savedBooking.getStatus()) {
                    case CONFIRMED:
                        message = "Đặt sân của bạn đã được XÁC NHẬN. Mã đặt sân: #" + 
                            String.format("%06d", savedBooking.getId()) + 
                            ". Vui lòng thanh toán để hoàn tất.";
                        break;
                    case CANCELLED:
                        message = "Đặt sân của bạn đã bị HỦY. Mã đặt sân: #" + 
                            String.format("%06d", savedBooking.getId()) + 
                            ". Nếu có thắc mắc, vui lòng liên hệ hỗ trợ.";
                        break;
                    case PENDING:
                        message = "Đặt sân của bạn đang CHỜ XÁC NHẬN. Mã đặt sân: #" + 
                            String.format("%06d", savedBooking.getId()) + 
                            ". Chúng tôi sẽ xử lý trong thời gian sớm nhất.";
                        break;
                }
                
                if (!message.isEmpty()) {
                    Notification notification = new Notification();
                    notification.setUserId(savedBooking.getUserId());
                    notification.setMessage(message);
                    notification.setStatus(Notification.Status.UNREAD);
                    notification.setCreatedAt(LocalDateTime.now());
                    notificationService.createNotification(notification);
                }
            } catch (Exception e) {
                logger.warn("Failed to create notification for booking status change: " + id, e);
            }
        }
        
        return savedBooking;
    }
    
    @DeleteMapping("/{id}")
    public void deleteBooking(@PathVariable Integer id) {
        bookingRepository.deleteById(id);
    }
}