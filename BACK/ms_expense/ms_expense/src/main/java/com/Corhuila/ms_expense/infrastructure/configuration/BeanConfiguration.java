package com.Corhuila.ms_expense.infrastructure.configuration;

import com.Corhuila.ms_expense.domain.ports.ExpenseRepositoryPort;
import com.Corhuila.ms_expense.domain.ports.ExpenseServicePort;
import com.Corhuila.ms_expense.application.usecases.ExpenseUseCase;
import com.Corhuila.ms_expense.infrastructure.adapters.output.external.CategoryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BeanConfiguration {

    @Bean
    public ExpenseServicePort expenseServicePort(ExpenseRepositoryPort expenseRepositoryPort, CategoryClient categoryClient) {
        return new ExpenseUseCase(expenseRepositoryPort, categoryClient);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}