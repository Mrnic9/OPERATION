package com.corhuila.ms_categories.infrastructure.adapters.input.rest;

import com.corhuila.ms_categories.domain.model.Category;
import com.corhuila.ms_categories.domain.model.dto.CategoryRequest;
import com.corhuila.ms_categories.domain.model.dto.CategoryResponse;
import com.corhuila.ms_categories.domain.model.dto.CategoryUpdateRequest;
import com.corhuila.ms_categories.domain.ports.CategoryServicePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@CrossOrigin(origins = "*")
public class CategoryController {

    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);
    private final CategoryServicePort categoryServicePort;

    @Autowired
    public CategoryController(CategoryServicePort categoryServicePort) {
        this.categoryServicePort = categoryServicePort;
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request) {
        logger.info("Solicitud recibida para crear categoría - Nombre: {}, Tipo: {}",
                request.getName(), request.getType());

        CategoryResponse response = categoryServicePort.createCategory(request);

        logger.info("Categoría creada exitosamente con ID: {}", response.getCategoryId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryUpdateRequest request) {
        logger.info("Solicitud recibida para actualizar categoría con ID: {}", id);

        CategoryResponse response = categoryServicePort.updateCategory(id, request);

        logger.info("Respuesta enviada para categoría actualizada con ID: {}", response.getCategoryId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        logger.info("Solicitud recibida para eliminar categoría con ID: {}", id);

        categoryServicePort.deleteCategory(id);

        logger.info("Respuesta enviada - Categoría eliminada lógicamente con ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        logger.info("Solicitud recibida para obtener todas las categorías");

        List<CategoryResponse> responses = categoryServicePort.getAllCategories();

        logger.info("Respuesta enviada con {} categorías", responses.size());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
        logger.info("Solicitud recibida para obtener categoría con ID: {}", id);

        CategoryResponse response = categoryServicePort.getCategoryById(id);

        logger.info("Respuesta enviada para categoría con ID: {}", response.getCategoryId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<CategoryResponse>> getCategoriesByType(@PathVariable Category.CategoryType type) {
        logger.info("Solicitud recibida para filtrar categorías por tipo: {}", type);

        List<CategoryResponse> responses = categoryServicePort.getCategoriesByType(type);

        logger.info("Respuesta enviada con {} categorías de tipo {}", responses.size(), type);
        return ResponseEntity.ok(responses);
    }
}
