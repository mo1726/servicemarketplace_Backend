package com.example.service_marketplace.Controller;

import com.example.service_marketplace.Dto.ReviewDto;
import com.example.service_marketplace.Service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewDto> createReview(@RequestBody ReviewDto dto) {
        return ResponseEntity.ok(reviewService.createReview(dto));
    }

    @GetMapping("/service/{serviceId}")
    public ResponseEntity<List<ReviewDto>> getReviewsByService(@PathVariable Long serviceId) {
        return ResponseEntity.ok(reviewService.getReviewsByService(serviceId));
    }

    @GetMapping("/average/{serviceId}")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long serviceId) {
        return ResponseEntity.ok(reviewService.getAverageRatingForService(serviceId));
    }

}
