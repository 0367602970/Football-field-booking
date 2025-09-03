package vti.group10.football_booking.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vti.group10.football_booking.model.Payment;
import vti.group10.football_booking.repository.PaymentRepository;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
    
    public Optional<Payment> getPaymentById(Integer id) {
        return paymentRepository.findById(id);
    }
    
    public List<Payment> getPaymentsByBookingId(Integer bookingId) {
        return paymentRepository.findByBookingId(bookingId);
    }
    
    public List<Payment> getPaymentsByStatus(Payment.Status status) {
        return paymentRepository.findByStatus(status);
    }
    
    public List<Payment> getPaymentsByMethod(Payment.PaymentMethod method) {
        return paymentRepository.findByPaymentMethod(method);
    }
    
    public Payment createPayment(Payment payment) {
        return paymentRepository.save(payment);
    }
    
    public Payment updatePayment(Integer id, Payment paymentDetails) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        
        payment.setAmount(paymentDetails.getAmount());
        payment.setPaymentMethod(paymentDetails.getPaymentMethod());
        payment.setStatus(paymentDetails.getStatus());
        
        return paymentRepository.save(payment);
    }

    @Transactional
    public void deletePaymentsByBookingId(Integer bookingId) {
        paymentRepository.deleteByBookingId(bookingId);
    }
}