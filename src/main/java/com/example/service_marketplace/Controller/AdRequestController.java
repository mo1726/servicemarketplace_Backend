package com.example.service_marketplace.Controller;

import com.example.service_marketplace.Dto.AdRequestDto;
import com.example.service_marketplace.Entity.AdRequest;
import com.example.service_marketplace.Mapper.AdRequestMapper;
import com.example.service_marketplace.Service.Impl.AdRequestServiceImpl;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/ad-requests")
@RequiredArgsConstructor
public class AdRequestController {

    private final AdRequestServiceImpl adService;
    private final AdRequestMapper mapper;

    @PostMapping
    public AdRequest createAdRequest(@RequestBody AdRequestDto dto) {
        return adService.createAdRequest(dto);
    }


    @GetMapping
    public ResponseEntity<List<AdRequestDto>> list(@RequestParam(required = false) String status) {
        return ResponseEntity.ok(
                adService.listAll(status).stream().map(mapper::toDto).toList()
        );
    }

    @GetMapping("/mine/{providerId}")
    public ResponseEntity<List<AdRequestDto>> mine(@PathVariable Long providerId) {
        return ResponseEntity.ok(
                adService.myRequests(providerId).stream().map(mapper::toDto).toList()
        );
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<?> approveAd(@PathVariable Long id) {
        adService.approve(id);
        return ResponseEntity.ok("Ad approved successfully");
    }


    @PatchMapping("/{id}/activate")
    public ResponseEntity<AdRequestDto> activate(@PathVariable Long id) {
        return ResponseEntity.ok(
                mapper.toDto(adService.activate(id))
        );
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<AdRequestDto> reject(@PathVariable Long id, @RequestBody RejectDto dto) {
        return ResponseEntity.ok(
                mapper.toDto(adService.reject(id, dto.getReason()))
        );
    }

    @Data
    static class CreateAdRequest {
        private Long serviceId;
        private Long providerId;
        private String plan;
        private Integer priority;
        private Double amount;
        private String paymentMethod;
        private String notes;
    }

    @Data
    static class RejectDto {
        private String reason;
    }
}
