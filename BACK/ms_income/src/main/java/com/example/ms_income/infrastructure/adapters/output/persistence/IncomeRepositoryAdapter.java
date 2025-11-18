package com.example.ms_income.infrastructure.adapters.output.persistence;

import com.example.ms_income.domain.model.Income;
import com.example.ms_income.domain.ports.IncomeRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class IncomeRepositoryAdapter implements IncomeRepositoryPort {

    private final IncomeRepository incomeRepository;

    @Autowired
    public IncomeRepositoryAdapter(IncomeRepository incomeRepository) {
        this.incomeRepository = incomeRepository;
    }

    @Override
    public Income save(Income income) {
        return incomeRepository.save(income);
    }

    @Override
    public Optional<Income> findByIdAndActiveTrue(Long id) {
        return incomeRepository.findByIncomeIdAndActiveTrue(id);
    }

    @Override
    public List<Income> findAllActive() {
        return incomeRepository.findAll().stream()
                .filter(Income::getActive)
                .toList();
    }

    @Override
    public List<Income> findByUserIdAndActiveTrue(Long userId) {
        return incomeRepository.findByUserIdAndActiveTrue(userId);
    }

    @Override
    public List<Income> findByUserIdAndIncomeCategoryIdAndActiveTrue(Long userId, Long categoryId) {
        return incomeRepository.findByUserIdAndIncomeCategoryIdAndActiveTrue(userId, categoryId);
    }

    @Override
    public List<Income> findByUserIdAndIncomeDateBetweenAndActiveTrue(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return incomeRepository.findByUserIdAndIncomeDateBetweenAndActiveTrue(userId, startDate, endDate);
    }

    @Override
    public List<Income> findByUserIdAndAmountBetweenAndActiveTrue(Long userId, BigDecimal minAmount, BigDecimal maxAmount) {
        return incomeRepository.findByUserIdAndAmountBetweenAndActiveTrue(userId, minAmount, maxAmount);
    }
}
