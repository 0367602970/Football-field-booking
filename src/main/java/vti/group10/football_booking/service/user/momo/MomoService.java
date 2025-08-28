package vti.group10.football_booking.service.user.momo;

import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import vti.group10.football_booking.dto.momo.MomoPaymentRequest;
import vti.group10.football_booking.dto.momo.MomoPaymentResponse;

@Service
@RequiredArgsConstructor
public class MomoService {
    @Value("${momo.partnerCode}")
    private String partnerCode;
    @Value("${momo.accessKey}")
    private String accessKey;
    @Value("${momo.secretKey}")
    private String secretKey;
    @Value("${momo.endpoint}")
    private String endpoint;
    @Value("${momo.returnUrl}")
    private String returnUrl;
    @Value("${momo.notifyUrl}")
    private String notifyUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public MomoPaymentResponse createPayment(Long amount, String orderId2, String orderInfo) throws Exception {
        String orderId = orderId2 + "-" + System.currentTimeMillis();
        String requestId = UUID.randomUUID().toString();
        String extraData = ""; // có thể Base64 encode nếu cần gửi thêm thông tin

        // raw string ký theo thứ tự MoMo yêu cầu
        String rawHash = "accessKey=" + accessKey
                + "&amount=" + amount
                + "&extraData=" + extraData
                + "&ipnUrl=" + notifyUrl
                + "&orderId=" + orderId
                + "&orderInfo=" + orderInfo
                + "&partnerCode=" + partnerCode
                + "&redirectUrl=" + returnUrl
                + "&requestId=" + requestId
                + "&requestType=captureWallet";

        String signature = hmacSHA256(rawHash, secretKey);

        MomoPaymentRequest request = MomoPaymentRequest.builder()
                .partnerCode(partnerCode)
                .requestId(requestId)
                .amount(amount)
                .orderId(orderId)
                .orderInfo(orderInfo)
                .redirectUrl(returnUrl)
                .ipnUrl(notifyUrl)
                .extraData(extraData)
                .requestType("captureWallet")
                .signature(signature)
                .lang("vi")
                .build();

        // Log request
        System.out.println("==== MoMo Payment Request ====");
        System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(request));

        ResponseEntity<MomoPaymentResponse> response =
                restTemplate.postForEntity(endpoint, request, MomoPaymentResponse.class);

        // Log response
        System.out.println("==== MoMo Payment Response ====");
        System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(response.getBody()));

        return response.getBody();
    }

    private String hmacSHA256(String data, String key) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        return HexFormat.of().formatHex(sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    }

}
