package com.example.service_marketplace.Service.Impl;

import com.example.service_marketplace.Dto.CategoryDto;

import com.example.service_marketplace.Entity.Category;
import com.example.service_marketplace.Mapper.CategoryMapper;
import com.example.service_marketplace.Repository.CategoryRepository;
import com.example.service_marketplace.Service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryDto createCategory(CategoryDto dto) {
        return categoryMapper.toDto(
                categoryRepository.save(categoryMapper.toEntity(dto))
        );
    }

    @Override
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }
    @Override
    public CategoryDto updateCategory(CategoryDto dto, Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
        if (dto.getName() != null && !dto.getName().isBlank()) {
            category.setName(dto.getName());
        }
        Category updatedCategory = categoryRepository.save(category);
        return categoryMapper.toDto(updatedCategory);


    }
    @Override
    public void  deleteCategory(long id) {
        categoryRepository.deleteById(id);
    }
}