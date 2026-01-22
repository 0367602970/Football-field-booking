package vti.group10.football_booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vti.group10.football_booking.dto.request.SepayWebhookRequest;
import vti.group10.football_booking.service.user.BookingFieldService;

@RestController
@RequestMapping("/api/webhook/sepay")
@RequiredArgsConstructor
public class SepayWebhookController {

    private final BookingFieldService bookingService;

    @PostMapping
    public ResponseEntity<String> handleWebhook(@RequestBody SepayWebhookRequest payload) {
        try {
            // Gọi service để xử lý thanh toán
            bookingService.handleSepayWebhook(payload);
            return ResponseEntity.ok("Webhook processed successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
