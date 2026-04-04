package com.almina.ecommerce.service.impl;

import com.almina.ecommerce.dto.product.CategoryDto;
import com.almina.ecommerce.entity.Category;
import com.almina.ecommerce.exception.ResourceNotFoundException;
import com.almina.ecommerce.mapper.EntityMapper;
import com.almina.ecommerce.repository.CategoryRepository;
import com.almina.ecommerce.service.CategoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EntityMapper entityMapper;

    @Override
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream().map(entityMapper::toCategoryDto).toList();
    }

    @Override
    public CategoryDto createCategory(CategoryDto request) {
        Category category = new Category();
        apply(category, request);
        return entityMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    public CategoryDto updateCategory(Long id, CategoryDto request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        apply(category, request);
        return entityMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    private void apply(Category category, CategoryDto request) {
        category.setName(request.name());
        category.setSlug(request.slug());
        category.setImageUrl(request.imageUrl());
        category.setDescription(request.description());
    }
}
