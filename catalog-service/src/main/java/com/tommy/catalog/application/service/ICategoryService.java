package com.tommy.catalog.application.service;

import com.tommy.catalog.application.dto.request.CategoryRequest;
import com.tommy.catalog.domain.entity.Category;

import java.util.List;
import java.util.UUID;

public interface ICategoryService {
    List<Category> getAllCategories();
    Category createCategory(CategoryRequest request);
    Category updateCategory(UUID id, CategoryRequest request);
    void deleteCategory(UUID id);
}
