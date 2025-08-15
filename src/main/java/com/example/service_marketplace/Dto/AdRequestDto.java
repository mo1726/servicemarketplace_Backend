package com.example.service_marketplace.Dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AdRequestDto {
    private Long id;
    private Long serviceId;
    private String serviceTitle;
    private Long providerId;
    private String providerName;
    private String status;
    private String plan;
    private Integer priority;
    private Double amount;
    private String paymentMethod;
    private String notes;
    private LocalDate requestedAt;
    private LocalDate approvedAt;
    private LocalDate startDate;
    private LocalDate endDate;
}
