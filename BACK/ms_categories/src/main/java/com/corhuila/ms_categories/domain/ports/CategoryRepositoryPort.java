package com.corhuila.ms_categories.domain.ports;

import com.corhuila.ms_categories.domain.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepositoryPort {
    Category save(Category category);
    Optional<Category> findByIdAndActiveTrue(Long id);
    List<Category> findAllActive();
    List<Category> findByTypeAndActiveTrue(Category.CategoryType type);
}
