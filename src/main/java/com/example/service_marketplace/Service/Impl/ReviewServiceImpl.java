package com.example.service_marketplace.Service.Impl;

import com.example.service_marketplace.Dto.ReviewDto;
import com.example.service_marketplace.Entity.Review;
import com.example.service_marketplace.Entity.User;
import com.example.service_marketplace.Entity.Service;
import com.example.service_marketplace.Mapper.ReviewMapper;
import com.example.service_marketplace.Repository.ReviewRepository;
import com.example.service_marketplace.Repository.ServiceRepository;
import com.example.service_marketplace.Repository.UserRepository;
import com.example.service_marketplace.Service.ReviewService;
import com.example.service_marketplace.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;

    @Override
    public ReviewDto createReview(ReviewDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Service service = serviceRepository.findById(dto.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));

        Review review = reviewMapper.toEntity(dto);
        review.setCreatedAt(LocalDateTime.now());
        review.setUser(user);
        review.setService(service);

        return reviewMapper.toDto(reviewRepository.save(review));
    }

    @Override
    public List<ReviewDto> getReviewsByService(Long serviceId) {
        return reviewRepository.findByServiceId(serviceId)
                .stream()
                .map(reviewMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public double getAverageRatingForService(Long serviceId) {
        List<Review> reviews = reviewRepository.findByServiceId(serviceId);
        return reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
    }
   
}
