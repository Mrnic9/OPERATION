package com.corhuila.ms_categories.infrastructure.configuration;

import com.corhuila.ms_categories.application.usecases.CategoryUseCase;
import com.corhuila.ms_categories.domain.ports.CategoryRepositoryPort;
import com.corhuila.ms_categories.domain.ports.CategoryServicePort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public CategoryServicePort categoryServicePort(CategoryRepositoryPort categoryRepositoryPort) {
        return new CategoryUseCase(categoryRepositoryPort);
    }
}
