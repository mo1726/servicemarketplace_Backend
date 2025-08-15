package com.example.service_marketplace.Dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ServiceDto {
    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;
    private Double price;
    private String location;
    private String imageUrl;

    private String categoryName;
    private String providerAvatar;
    private String providerName;
    private String fullName;

    private Long providerId;
    private Long categoryId;

    private UserDto provider;

    // ✅ Ad fields
    private Boolean adActive;
    private LocalDate adStartDate;
    private LocalDate adEndDate;
    private Integer adPriority;
}
