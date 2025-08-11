package com.example.service_marketplace.Repository;

import com.example.service_marketplace.Entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByServiceId(Long serviceId);
}
