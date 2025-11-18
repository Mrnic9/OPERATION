package com.corhuila.ms_categories.infrastructure.adapters.output.persistence;

import com.corhuila.ms_categories.domain.model.Category;
import com.corhuila.ms_categories.domain.ports.CategoryRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CategoryRepositoryAdapter implements CategoryRepositoryPort {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryRepositoryAdapter(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public Optional<Category> findByIdAndActiveTrue(Long id) {
        return categoryRepository.findByCategoryIdAndActiveTrue(id);
    }

    @Override
    public List<Category> findAllActive() {
        return categoryRepository.findAllActive();
    }

    @Override
    public List<Category> findByTypeAndActiveTrue(Category.CategoryType type) {
        return categoryRepository.findByTypeAndActiveTrue(type);
    }
}
