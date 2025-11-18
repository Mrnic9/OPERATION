package com.corhuila.ms_categories.domain.ports;

import com.corhuila.ms_categories.domain.model.Category;
import com.corhuila.ms_categories.domain.model.dto.CategoryRequest;
import com.corhuila.ms_categories.domain.model.dto.CategoryResponse;
import com.corhuila.ms_categories.domain.model.dto.CategoryUpdateRequest;

import java.util.List;

public interface CategoryServicePort {
    CategoryResponse createCategory(CategoryRequest request);
    CategoryResponse updateCategory(Long id, CategoryUpdateRequest request);
    void deleteCategory(Long id);
    List<CategoryResponse> getAllCategories();
    List<CategoryResponse> getCategoriesByType(Category.CategoryType type);
    CategoryResponse getCategoryById(Long id);
}
