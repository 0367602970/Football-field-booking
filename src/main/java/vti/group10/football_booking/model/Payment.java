package vti.group10.football_booking.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne @JoinColumn(name = "booking_id")
    private Booking booking;

    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private Method paymentMethod;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private Status status;

    // Một số trường trace MoMo
    private String momoOrderId;   // orderId bạn gửi sang MoMo
    private String momoRequestId; // requestId bạn gửi sang MoMo
    private String momoTransId;   // transId MoMo trả về
    private Integer resultCode;    // resultCode MoMo
    private String message;        // message MoMo

    public enum Method {
        MOMO
    }

    public enum Status {
        SUCCESS, FAILED
    }
}
