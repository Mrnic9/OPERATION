package com.Corhuila.ms_expense.infrastructure.adapters.output.external;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

@Component
public class CategoryClient {

    private static final Logger logger = LoggerFactory.getLogger(CategoryClient.class);
    private final RestTemplate restTemplate;
    private final String categoriesServiceUrl;

    public CategoryClient(
            RestTemplate restTemplate,
            @Value("${categories.service.url}") String categoriesServiceUrl) {
        this.restTemplate = restTemplate;
        this.categoriesServiceUrl = categoriesServiceUrl;
    }

    public CategoryResponse getCategoryById(Long categoryId) {
        try {
            logger.info("Consultando categoría con ID: {} en {}", categoryId, categoriesServiceUrl);
            String url = categoriesServiceUrl + "/api/v1/categories/" + categoryId;
            CategoryResponse response = restTemplate.getForObject(url, CategoryResponse.class);
            logger.info("Categoría obtenida: {}", response != null ? response.getName() : "null");
            return response;
        } catch (HttpClientErrorException.NotFound e) {
            logger.warn("Categoría con ID {} no encontrada", categoryId);
            return null;
        } catch (Exception e) {
            logger.error("Error al consultar categoría con ID {}: {}", categoryId, e.getMessage());
            // En caso de error de comunicación, retornar null y permitir que continúe
            // (modo degradado - tolerancia a fallos)
            return null;
        }
    }

    public boolean validateExpenseCategoryExists(Long categoryId) {
        CategoryResponse category = getCategoryById(categoryId);

        if (category == null) {
            logger.warn("Categoría con ID {} no existe", categoryId);
            return false;
        }

        if (!category.getActive()) {
            logger.warn("Categoría con ID {} no está activa", categoryId);
            return false;
        }

        if (category.getType() != CategoryResponse.CategoryType.EXPENSE) {
            logger.warn("Categoría con ID {} no es de tipo EXPENSE, es {}", categoryId, category.getType());
            return false;
        }

        logger.info("Categoría con ID {} validada correctamente", categoryId);
        return true;
    }
}
