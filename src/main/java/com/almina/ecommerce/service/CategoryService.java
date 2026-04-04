package com.almina.ecommerce.service;

import com.almina.ecommerce.dto.product.CategoryDto;
import java.util.List;

public interface CategoryService {
    List<CategoryDto> getAllCategories();
    CategoryDto createCategory(CategoryDto request);
    CategoryDto updateCategory(Long id, CategoryDto request);
    void deleteCategory(Long id);
}
