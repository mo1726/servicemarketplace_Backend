package com.example.service_marketplace.Controller;

import com.example.service_marketplace.Dto.CategoryDto;
import com.example.service_marketplace.Service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryDto> create(@RequestBody CategoryDto dto) {
        return ResponseEntity.ok(categoryService.createCategory(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> update(@PathVariable Long id, @RequestBody CategoryDto dto) {
        return ResponseEntity.ok(categoryService.updateCategory(dto, id));
    }

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAll() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<CategoryDto> delete(@PathVariable long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok().build();
    }
}

