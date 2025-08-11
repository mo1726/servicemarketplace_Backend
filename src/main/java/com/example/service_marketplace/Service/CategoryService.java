package com.example.service_marketplace.Service;

import com.example.service_marketplace.Dto.CategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(CategoryDto dto);
    List<CategoryDto> getAllCategories();
    CategoryDto updateCategory(CategoryDto dto, Long id);
    void deleteCategory(long id);
}
