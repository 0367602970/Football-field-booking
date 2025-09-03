package vti.group10.football_booking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import vti.group10.football_booking.model.Payment;
import vti.group10.football_booking.model.Notification;
import vti.group10.football_booking.model.Booking;
import vti.group10.football_booking.repository.PaymentRepository;
import vti.group10.football_booking.repository.BookingRepository;
import vti.group10.football_booking.service.NotificationService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class PaymentController {
    
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    @GetMapping
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public Payment getPaymentById(@PathVariable Integer id) {
        return paymentRepository.findById(id).orElse(null);
    }
    
    @PostMapping
    public Payment createPayment(@RequestBody Payment payment) {
        if (payment.getPaymentDate() == null) {
            payment.setPaymentDate(LocalDateTime.now());
        }
        Payment savedPayment = paymentRepository.save(payment);
        
        // Tạo thông báo cho payment mới
        try {
            Booking booking = bookingRepository.findById(savedPayment.getBookingId()).orElse(null);
            if (booking != null) {
                Notification notification = new Notification();
                notification.setUserId(booking.getUserId());
                notification.setMessage("Yêu cầu thanh toán mới đã được tạo. Mã giao dịch: #" + 
                    String.format("%08d", savedPayment.getId()) + 
                    ". Số tiền: " + String.format("%,.0f", savedPayment.getAmount()) + " VND. Trạng thái: Chờ thanh toán.");
                notification.setStatus(Notification.Status.UNREAD);
                notification.setCreatedAt(LocalDateTime.now());
                notificationService.createNotification(notification);
            }
        } catch (Exception e) {
            logger.warn("Failed to create notification for new payment: " + savedPayment.getId(), e);
        }
        
        return savedPayment;
    }
    
    @PutMapping("/{id}")
    public Payment updatePayment(@PathVariable Integer id, @RequestBody Payment paymentDetails) {
        Payment existingPayment = paymentRepository.findById(id).orElseThrow();
        String oldStatus = existingPayment.getStatus().toString();
        
        Payment payment = paymentRepository.findById(id).orElseThrow();
        payment.setAmount(paymentDetails.getAmount());
        payment.setPaymentMethod(paymentDetails.getPaymentMethod());
        payment.setStatus(paymentDetails.getStatus());
        payment.setMessage(paymentDetails.getMessage());
        payment.setMomoOrderId(paymentDetails.getMomoOrderId());
        payment.setMomoRequestId(paymentDetails.getMomoRequestId());
        payment.setMomoTransId(paymentDetails.getMomoTransId());
        payment.setResultCode(paymentDetails.getResultCode());
        
        Payment savedPayment = paymentRepository.save(payment);
        
        // Tạo thông báo khi thay đổi trạng thái thanh toán
        String newStatus = savedPayment.getStatus().toString();
        if (!oldStatus.equals(newStatus)) {
            try {
                Booking booking = bookingRepository.findById(savedPayment.getBookingId()).orElse(null);
                if (booking != null) {
                    String message = "";
                    switch (savedPayment.getStatus()) {
                        case SUCCESS:
                            message = "Thanh toán THÀNH CÔNG! Mã giao dịch: #" + 
                                String.format("%08d", savedPayment.getId()) + 
                                ". Số tiền: " + String.format("%,.0f", savedPayment.getAmount()) + " VND. " +
                                "Đặt sân của bạn đã được xác nhận hoàn tất.";
                            break;
                        case FAILED:
                            message = "Thanh toán THẤT BẠI! Mã giao dịch: #" + 
                                String.format("%08d", savedPayment.getId()) + 
                                ". Số tiền: " + String.format("%,.0f", savedPayment.getAmount()) + " VND. " +
                                "Vui lòng thử lại hoặc chọn phương thức thanh toán khác.";
                            break;
                        case PENDING:
                            message = "Thanh toán đang CHỜ XỬ LÝ. Mã giao dịch: #" + 
                                String.format("%08d", savedPayment.getId()) + 
                                ". Số tiền: " + String.format("%,.0f", savedPayment.getAmount()) + " VND. " +
                                "Chúng tôi đang xử lý thanh toán của bạn.";
                            break;
                    }
                    
                    if (!message.isEmpty()) {
                        Notification notification = new Notification();
                        notification.setUserId(booking.getUserId());
                        notification.setMessage(message);
                        notification.setStatus(Notification.Status.UNREAD);
                        notification.setCreatedAt(LocalDateTime.now());
                        notificationService.createNotification(notification);
                    }
                }
            } catch (Exception e) {
                logger.warn("Failed to create notification for payment status change: " + id, e);
            }
        }
        
        return savedPayment;
    }
    
    @DeleteMapping("/{id}")
    public void deletePayment(@PathVariable Integer id) {
        paymentRepository.deleteById(id);
    }
}