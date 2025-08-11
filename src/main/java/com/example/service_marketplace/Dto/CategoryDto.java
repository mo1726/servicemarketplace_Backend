package com.example.service_marketplace.Dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryDto {
    private Long id;

    @NotBlank(message = "Category name is required")
    private String name;
}
