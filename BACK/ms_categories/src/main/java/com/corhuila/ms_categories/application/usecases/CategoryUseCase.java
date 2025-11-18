package com.corhuila.ms_categories.application.usecases;

import com.corhuila.ms_categories.domain.model.Category;
import com.corhuila.ms_categories.domain.model.dto.CategoryRequest;
import com.corhuila.ms_categories.domain.model.dto.CategoryResponse;
import com.corhuila.ms_categories.domain.model.dto.CategoryUpdateRequest;
import com.corhuila.ms_categories.domain.ports.CategoryRepositoryPort;
import com.corhuila.ms_categories.domain.ports.CategoryServicePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryUseCase implements CategoryServicePort {

    private static final Logger logger = LoggerFactory.getLogger(CategoryUseCase.class);
    private final CategoryRepositoryPort categoryRepositoryPort;

    @Autowired
    public CategoryUseCase(CategoryRepositoryPort categoryRepositoryPort) {
        this.categoryRepositoryPort = categoryRepositoryPort;
    }

    @Override
    public CategoryResponse createCategory(CategoryRequest request) {
        logger.info("Iniciando creación de categoría: {} de tipo: {}", request.getName(), request.getType());

        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .type(request.getType())
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Category savedCategory = categoryRepositoryPort.save(category);
        logger.info("Categoría creada exitosamente con ID: {}", savedCategory.getCategoryId());

        return mapToResponse(savedCategory);
    }

    @Override
    public CategoryResponse updateCategory(Long id, CategoryUpdateRequest request) {
        logger.info("Iniciando actualización de categoría con ID: {}", id);

        Category category = categoryRepositoryPort.findByIdAndActiveTrue(id)
                .orElseThrow(() -> {
                    logger.error("No se encontró categoría activa con ID: {}", id);
                    return new RuntimeException("Categoría no encontrada con ID: " + id);
                });

        if (request.getName() != null) {
            logger.info("Actualizando nombre de '{}' a '{}'", category.getName(), request.getName());
            category.setName(request.getName());
        }
        if (request.getDescription() != null) {
            logger.info("Actualizando descripción de la categoría");
            category.setDescription(request.getDescription());
        }
        if (request.getType() != null) {
            logger.info("Actualizando tipo de {} a {}", category.getType(), request.getType());
            category.setType(request.getType());
        }
        category.setUpdatedAt(LocalDateTime.now());

        Category updatedCategory = categoryRepositoryPort.save(category);
        logger.info("Categoría actualizada exitosamente con ID: {}", updatedCategory.getCategoryId());

        return mapToResponse(updatedCategory);
    }

    @Override
    public void deleteCategory(Long id) {
        logger.info("Iniciando eliminación lógica de categoría con ID: {}", id);

        Category category = categoryRepositoryPort.findByIdAndActiveTrue(id)
                .orElseThrow(() -> {
                    logger.error("No se encontró categoría activa con ID: {} para eliminar", id);
                    return new RuntimeException("Categoría no encontrada con ID: " + id);
                });

        category.setActive(false);
        category.setUpdatedAt(LocalDateTime.now());
        categoryRepositoryPort.save(category);

        logger.info("Categoría eliminada lógicamente exitosamente con ID: {}", id);
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        logger.info("Obteniendo todas las categorías activas");

        List<CategoryResponse> categories = categoryRepositoryPort.findAllActive().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        logger.info("Se encontraron {} categorías activas", categories.size());
        return categories;
    }

    @Override
    public List<CategoryResponse> getCategoriesByType(Category.CategoryType type) {
        logger.info("Filtrando categorías por tipo: {}", type);

        List<CategoryResponse> categories = categoryRepositoryPort.findByTypeAndActiveTrue(type).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        logger.info("Se encontraron {} categorías de tipo {}", categories.size(), type);
        return categories;
    }

    @Override
    public CategoryResponse getCategoryById(Long id) {
        logger.info("Buscando categoría con ID: {}", id);

        Category category = categoryRepositoryPort.findByIdAndActiveTrue(id)
                .orElseThrow(() -> {
                    logger.error("No se encontró categoría activa con ID: {}", id);
                    return new RuntimeException("Categoría no encontrada con ID: " + id);
                });

        logger.info("Categoría encontrada: {}", category.getName());
        return mapToResponse(category);
    }

    private CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .categoryId(category.getCategoryId())
                .name(category.getName())
                .description(category.getDescription())
                .type(category.getType())
                .active(category.getActive())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}
