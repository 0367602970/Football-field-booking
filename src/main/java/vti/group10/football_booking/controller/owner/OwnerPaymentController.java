package vti.group10.football_booking.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import vti.group10.football_booking.dto.PaymentResponse;
import vti.group10.football_booking.service.OwnerPaymentService;

@RestController
@RequestMapping("/api/owners")
@RequiredArgsConstructor
public class OwnerPaymentController {
    private final OwnerPaymentService ownerPaymentService;

    @GetMapping("/{ownerId}/payments")
    public List<PaymentResponse> getPaymentsByOwner(@PathVariable Integer ownerId) {
        return ownerPaymentService.getPaymentsByOwner(ownerId);
    }
}
