package vti.group10.football_booking.controller.user;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vti.group10.football_booking.dto.momo.MomoPaymentResponse;
import vti.group10.football_booking.model.Payment;
import vti.group10.football_booking.service.user.PaymentService;
import vti.group10.football_booking.service.user.momo.MomoService;

@RestController
@RequestMapping("/api/payment/momo")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private final MomoService momoService;

    // Tạo thanh toán
    // *** CHÚ Ý: ***
    // Trước khi thực hiện thanh toán cần tải app test MoMo về điện thoại theo hướng dẫn trong link này https://developers.momo.vn/v3/docs/payment/onboarding/test-instructions
    // Khi thanh toán truyền lên amount, orderId (id bảng booking), orderInfo (mô tả)
    //VD: http://localhost:8080/api/payment/momo/create?amount=500000&orderId=1&orderInfo=ThanhToanTest
    /*
     * Hướng dẫn thanh toán MoMo:
     * B1: Cài ngrok để có https://xxxxxx.ngrok.io tại https://ngrok.com/download
     * B2: Giải nén, sau đó trong cmd cd vào địa chỉ thư mục vừa giải nén
     * B3: Chạy lệnh: ngrok.exe config add-authtoken <TOKEN_CỦA_BẠN> (Token có thể đăng nhập vào link https://dashboard.ngrok.com/ để lấy)
     * B4: Chạy lệnh ngrok http 8080 sẽ ra kết quả dạng https://8e21-113-22-34-55.ngrok-free.app
     * B5: Copy và dán vào dòng "momo.notifyUrl" file application.properties của project (dán trước /api/payment/momo/ipn)
     * B6: Chạy lại project, gọi API tạo thanh toán như VD trên, sau đó vào link redirectUrl để thanh toán
     */
    @PostMapping("/create")
    public ResponseEntity<MomoPaymentResponse> createPayment(
            @RequestParam Long amount,
            @RequestParam String orderId,
            @RequestParam String orderInfo
    ) throws Exception {
        MomoPaymentResponse res = momoService.createPayment(amount, orderId, orderInfo);
        return ResponseEntity.ok(res);
    }

    // Callback khi user thanh toán xong
    @GetMapping("/return")
    public ResponseEntity<String> momoReturn(@RequestParam Map<String, String> params) {
        System.out.println("==== MoMo Return Params ====");
        System.out.println(params);
        return ResponseEntity.ok("Momo return: " + params.toString());
    }

    // IPN notify server → để MoMo gọi ngầm
    @PostMapping("/ipn")
    public ResponseEntity<String> momoIpn(@RequestBody Map<String, Object> body) {
        System.out.println("==== MoMo IPN Body ====");
        System.out.println(body);

        try {
            Payment saved = paymentService.saveMomoPayment(body);
            return ResponseEntity.ok("Payment saved with id: " + saved.getId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: " + e.getMessage());
        }
    }
}
