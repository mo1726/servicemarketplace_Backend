package com.example.service_marketplace.Service;

import com.example.service_marketplace.Dto.ReviewDto;
import java.util.List;

public interface ReviewService {
    ReviewDto createReview(ReviewDto dto);
    List<ReviewDto> getReviewsByService(Long serviceId);
    double getAverageRatingForService(Long serviceId);


}
