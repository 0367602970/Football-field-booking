package vti.group10.football_booking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private Double amount;
    
    @Column(name = "payment_date")
    private LocalDateTime paymentDate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;
    
    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;
    
    @Column(name = "booking_id")
    private Integer bookingId;
    
    // MoMo payment fields
    private String message;
    
    @Column(name = "momo_order_id")
    private String momoOrderId;
    
    @Column(name = "momo_request_id")
    private String momoRequestId;
    
    @Column(name = "momo_trans_id")
    private String momoTransId;
    
    @Column(name = "result_code")
    private Integer resultCode;
    
    public enum PaymentMethod {
        BANK, CASH, MOMO, ZALOPAY
    }
    
    public enum Status {
        FAILED, PENDING, SUCCESS
    }
}