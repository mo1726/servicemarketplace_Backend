package com.example.service_marketplace.Dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewDto {
    private Long id;
    private int rating; // 1-5 stars
    private String comment;
    private Long userId;
    private Long serviceId;
    private LocalDateTime createdAt;
    private String userFullName;
    private String userAvatarUrl;
}
