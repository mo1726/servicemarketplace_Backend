package com.example.service_marketplace.Dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class BookingDto {
    private Long id;

    @NotNull(message = "Date is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;

    private String status;
    private String notes;

    private Long userId;
    private Long serviceId;

    private Double price; // or BigDecimal


    private ServiceDto service;
    private UserDto user;

    private String userFullName;
    private String userEmail;
    private String userPhone;
    private String userAvatarUrl;


}
