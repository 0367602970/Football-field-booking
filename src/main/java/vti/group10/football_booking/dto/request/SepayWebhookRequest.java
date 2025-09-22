package vti.group10.football_booking.dto.request;

import lombok.Data;

@Data
public class SepayWebhookRequest {
    private Long id;               // ID giao dịch trên SePay
    private String gateway;        // Brand name của ngân hàng
    private String transactionDate;
    private String accountNumber;
    private String code;
    private String content;        // Nội dung chuyển khoản
    private String transferType;   // in/out
    private Double transferAmount;
    private Double accumulated;
    private String subAccount;
    private String referenceCode;
    private String description;
}
