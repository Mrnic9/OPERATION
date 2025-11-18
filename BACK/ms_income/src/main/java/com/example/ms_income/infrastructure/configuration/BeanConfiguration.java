package com.example.ms_income.infrastructure.configuration;

import com.example.ms_income.domain.ports.IncomeRepositoryPort;
import com.example.ms_income.domain.ports.IncomeServicePort;
import com.example.ms_income.application.usecases.IncomeUseCase;
import com.example.ms_income.infrastructure.adapters.output.external.CategoryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BeanConfiguration {

    @Bean
    public IncomeServicePort incomeServicePort(IncomeRepositoryPort incomeRepositoryPort, CategoryClient categoryClient) {
        return new IncomeUseCase(incomeRepositoryPort, categoryClient);
    }

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
