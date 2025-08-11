package com.example.service_marketplace.Mapper;

import com.example.service_marketplace.Dto.CategoryDto;
import com.example.service_marketplace.Entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDto toDto(Category category);
    Category toEntity(CategoryDto dto);
}
